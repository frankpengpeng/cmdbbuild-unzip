/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.dao;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.core.dao.data.PlanAndClasseMapperService;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.repository.ClassStructureChangedEvent;
import org.cmdbuild.dao.driver.repository.DaoEventService;
import org.cmdbuild.workflow.model.Process;
import org.cmdbuild.workflow.inner.ProcessRepository;
import org.cmdbuild.cache.CmCache;
import static org.cmdbuild.workflow.core.dao.data.PlanAndClasseMapperService.DEFAULT_PLAN_ID;
import org.cmdbuild.workflow.core.dao.data.PlanServiceDelegateSupplier;
import org.cmdbuild.workflow.inner.PlanUpdatedEvent;

@Component
public class ProcessRepositoryImpl implements ProcessRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final PlanAndClasseMapperService converter;
    private final OperationUserSupplier userSupplier;

    private final CmCache<Process> planClasseByPlanId;
    private final Holder<List<Process>> allPlanClasses;

    public ProcessRepositoryImpl(OperationUserSupplier userSupplier, PlanAndClasseMapperService toPlanClasseService, DaoService dao, CacheService cacheService, DaoEventService eventService, PlanServiceDelegateSupplier delegates) {
        this.userSupplier = checkNotNull(userSupplier);
        this.dao = checkNotNull(dao);
        this.converter = checkNotNull(toPlanClasseService);
        planClasseByPlanId = cacheService.newCache("plan_classe_repo_cache_by_plan_id");//TODO drop cache if xpdl change
        allPlanClasses = cacheService.newHolder("plan_classe_repo_cache_all_classes");//TODO drop cache if xpdl change
        eventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleClassStructureChangedEvent(ClassStructureChangedEvent event) {
                invalidateCache();
            }

        });
        delegates.getAllDelegates().forEach((d) -> {
            d.getEventBus().register(new Object() {
                @Subscribe
                public void handlePlanUpdatedEvent(PlanUpdatedEvent event) {
                    invalidateCache();
                }
            });
        });
    }

    private void invalidateCache() {
        planClasseByPlanId.invalidateAll();
        allPlanClasses.invalidate();
    }

    @Override
    public Process getPlanClasseByClassAndPlanId(String classId, String planId) {
        return planClasseByPlanId.get(planId, () -> {
            return converter.classeAndPlanIdToPlanClasse(getClasseByName(classId), planId);
        });
    }

    @Override
    public Process getPlanClasseByProviderAndPlanId(String provider, String planId) {
        return planClasseByPlanId.get(planId, () -> {
            return converter.classeAndPlanIdToPlanClasse(getClasseByName(converter.getClasseIdByProviderAndPlanId(provider, planId)), planId);
        });
    }

    @Override
    public List<Process> getAllPlanClasses() {
        return allPlanClasses.get(() -> {
            logger.debug("getting all process classes");
            return dao.getAllClasses().stream().filter(Classe::isProcess).map(this::classToPlanClasse).collect(toList());
        }).stream().filter(this::isProcessAndUserCanRead).collect(toList());
    }

    @Override
    @Nullable
    public Process getPlanClasseOrNull(String classId) {
        logger.trace("getting process class with name = {}", classId);
        Classe classe = dao.getClasseOrNull(classId);
        if (classe == null) {
            return null;
        } else {
            checkArgument(isProcessAndUserCanRead(classe), "current user = %s cannot access class = %s", userSupplier.getUsername(), classId);
            return classToPlanClasse(classe);
        }
    }

    private Classe getClasseByName(String classeId) {
        Classe classe = dao.getClasse(classeId);
        checkArgument(isProcessAndUserCanRead(classe), "classe %s is not a process", classe);
        return classe;
    }

    private boolean isProcessAndUserCanRead(Classe classe) {
        UserPrivileges privilegeContext = userSupplier.getPrivileges();
//        return classe.isProcess() && (classe.getName().equals(BASE_PROCESS_CLASS_NAME) || privilegeContext.hasReadAccess(classe));
        return classe.isProcess() && privilegeContext.hasReadAccess(classe);
    }

    @Override
    public Process classToPlanClasse(Classe classe) {
        return converter.classeAndPlanIdToPlanClasse(classe, DEFAULT_PLAN_ID);
    }

}
