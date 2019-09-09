/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.Collection;
import java.util.List;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.bim.bimserverclient.BimserverClientService;
import org.cmdbuild.bim.bimserverclient.BimserverProject;
import static org.cmdbuild.bim.utils.BimConstants.IFC_FORMAT_DEFAULT;
import org.cmdbuild.config.BimserverConfiguration;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.CardIdAndClassName;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.services.MinionStatus;
import org.springframework.stereotype.Component;
import org.cmdbuild.services.MinionComponent;
import static org.cmdbuild.services.MinionStatus.MS_READY;
import static org.cmdbuild.services.MinionStatus.MS_DISABLED;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
@MinionComponent(name = "BIM Service", configBean = BimserverConfiguration.class)
public class BimServiceImpl implements BimService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final BimserverConfiguration configuration;
    private final NavTreeService navTreeService;
    private final BimObjectRepository objectRepository;
    private final BimProjectRepository projectRepository;
    private final BimserverClientService bimserverClient;

    public BimServiceImpl(DaoService dao, BimserverConfiguration configuration, NavTreeService navTreeService, BimObjectRepository objectRepository, BimProjectRepository projectRepository, BimserverClientService bimserverClient) {
        this.configuration = checkNotNull(configuration);
        this.navTreeService = checkNotNull(navTreeService);
        this.objectRepository = checkNotNull(objectRepository);
        this.projectRepository = checkNotNull(projectRepository);
        this.bimserverClient = checkNotNull(bimserverClient);
        this.dao = checkNotNull(dao);

    }

    public MinionStatus getServiceStatus() {
        if (!isEnabled()) {
            return MS_DISABLED;
        } else {
            return MS_READY;
        }
    }

    @Override
    public boolean isEnabled() {
        return configuration.isEnabled();
    }

    @Override
    @Nullable
    public BimObject getBimObjectForGlobalIdOrNull(String globalId) {
        return objectRepository.getBimObjectForGlobalIdOrNull(globalId);
    }

    @Override
    public Collection<BimProject> getAllProjects() {
        return projectRepository.getAllProjects();
    }

    @Override
    public BimProject getProjectById(long id) {
        return projectRepository.getProjectById(id);
    }

    @Override
    public void deleteProjectById(long id) {
        projectRepository.deleteProjectById(id);
    }

    @Override
    public BimProject createProject(BimProject bimProject) {
        BimserverProject bimserverProject = bimserverClient.createProject(bimProject.getName(), bimProject.getDescription(), IFC_FORMAT_DEFAULT);
        bimProject = BimProjectImpl.copyOf(bimProject).withProjectId(bimserverProject.getProjectId()).build();
        return projectRepository.createProject(bimProject);
    }

    @Override
    public BimProject updateProject(BimProject bimProject) {
        BimProject current = getProjectById(bimProject.getId());
        bimProject = BimProjectImpl.copyOf(current)
                .withActive(bimProject.isActive())
                .withDescription(bimProject.getDescription())
                .withImportMapping(bimProject.getImportMapping())
                .build();
        //TODO handle active/inactive on bim server
        return projectRepository.updateProject(bimProject);
    }

    @Override
    public BimProjectExt createProjectExt(BimProjectExt data) {
        BimProject created = createProject(data);
        if (data.hasOwner()) {
            objectRepository.createBimObjectForProject(created, data.getOwner());
        }
        return getProjectExt(created.getId());
    }

    @Override
    public BimProjectExt updateProjectExt(BimProjectExt data) {
        BimObject current = objectRepository.getBimObjectForProjectOrNull(data);
        updateProject(data);
        if (current == null && data.hasOwner()) {
            objectRepository.createBimObjectForProject(data, data.getOwner());
        }
        return getProjectExt(data.getId());
    }

    @Override
    public boolean hasBim(Classe classe) {
        NavTree navTree = getNavTreeOrNull();
        return navTree != null && navTree.getData().getThisNodeAndAllDescendants().stream().anyMatch(n -> equal(n.getTargetClassName(), classe.getName()));
    }

    @Override
    @Nullable
    public BimObject getBimObjectForCardOrNull(CardIdAndClassName card) {
        return objectRepository.getBimObjectForCardOrNull(card);
    }

    @Override
    @Nullable
    public BimObject getBimObjectForCardOrViaNavTreeOrNull(CardIdAndClassName card) {
        BimObject obj = getBimObjectForCardOrNull(card);
        if (obj != null) {
            return obj;
        } else {
            NavTree navTree = getNavTreeOrNull();
            if (navTree != null) {
                NavTreeNode node = navTree.getData().getThisNodeAndAllDescendants().stream().filter(n -> dao.getType(card).equalToOrDescendantOf(n.getTargetClassName())).collect(toOptional()).orElse(null);
                if (node != null && node.hasParent()) {
                    CMRelation relation = dao.selectAll().from(dao.getDomain(node.getDomainName())).where(node.getDirect() ? ATTR_IDOBJ2 : ATTR_IDOBJ1, EQ, card.getId()).getRelationOrNull();
                    if (relation != null) {
                        return getBimObjectForCardOrViaNavTreeOrNull(relation.getRelationWithSource(card.getId()).getTargetCard());
                    }
                }
            }
            return null;
        }
    }

    @Override
    @Nullable
    public BimObject getBimObjectForProjectOrNull(BimProject bimProject) {
        return objectRepository.getBimObjectForProjectOrNull(bimProject);
    }

    @Override
    public DataHandler downloadIfcFile(long projectId, @Nullable String ifcFormat) {
        BimProject project = projectRepository.getProjectById(projectId);
        String revisionId = bimserverClient.getLastRevisionOfProject(project.getProjectId());
        return bimserverClient.downloadIfc(revisionId, defaultIfBlank(ifcFormat, IFC_FORMAT_DEFAULT));
    }

    @Override
    public void uploadIfcFile(long projectId, DataHandler dataHandler, @Nullable String ifcFormat) {
        BimProject project = projectRepository.getProjectById(projectId);
        bimserverClient.uploadIfc(project.getProjectId(), dataHandler, trimToNull(ifcFormat));
    }

    @Override
    public BimProjectExt getProjectExt(long projectId) {
        BimProject project = getProjectById(projectId);
        BimObject bimObject = getBimObjectForProjectOrNull(project);
        return new BimProjectExtImpl(project, bimObject);
    }

    @Override
    public List<BimProjectExt> getAllProjectsAndObjects() {
        return getAllProjects().stream().map((p) -> {
            BimObject bimObject = getBimObjectForProjectOrNull(p);//TODO this is inefficent, just a quick fix; change bim service to run a join query or something else
            return new BimProjectExtImpl(p, bimObject);
        }).collect(toImmutableList());
    }

    @Override
    public BimObject createBimObjectForProject(BimProject bimProject, CardIdAndClassName card) {
        return objectRepository.createBimObjectForProject(bimProject, card);
    }

    @Nullable
    private NavTree getNavTreeOrNull() {
        return navTreeService.getTreeOrNull(BIM_NAV_TREE);
    }

}
