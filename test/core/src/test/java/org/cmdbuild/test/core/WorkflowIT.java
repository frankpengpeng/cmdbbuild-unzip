/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Lists.transform;
import static java.util.Collections.emptyMap;
import java.util.List;
import static java.util.stream.Collectors.toSet;
import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantDataImpl;
import org.cmdbuild.auth.grant.GrantDataRepository;
import static org.cmdbuild.auth.grant.GrantMode.GM_WRITE;
import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.workflow.model.TaskAttribute;
import org.cmdbuild.workflow.model.TaskDefinition;
import org.cmdbuild.workflow.type.ReferenceType;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
public class WorkflowIT {

    private final String xpdl_v1 = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/NewTestProcess_V1.xpdl")),
            xpdl_v2 = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/NewTestProcess_V2.xpdl")),
            rfc = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/rfc.xpdl")),
            currentGroup = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/CurrentGroupProcess.xpdl"));

    private final DaoService dao;
    private final WorkflowService workflowService;
    private final GlobalConfigService configService;
    private final SessionService sessionService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final GrantDataRepository repository;

    public WorkflowIT(DaoService dao, WorkflowService workflowService, GlobalConfigService configService, SessionService sessionService, UserRepository userRepository, RoleRepository roleRepository, GrantDataRepository repository) {
        this.dao = checkNotNull(dao);
        this.workflowService = checkNotNull(workflowService);
        this.configService = checkNotNull(configService);
        this.sessionService = checkNotNull(sessionService);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.repository = checkNotNull(repository);
    }

    @Before
    public void init() {
        prepareTuid();
        configService.putString("org.cmdbuild.workflow.enabled", "true");
        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired("admin"));
    }

    @Test
    public void testCurrentGroup() {
        Classe process = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("CurrentGroupProcess")).withParent(dao.getClasse("Activity")).build());
        Role changeManager = roleRepository.create(RoleImpl.builder().withName(tuid("ChangeManager")).build());
        Role helpdesk = roleRepository.create(RoleImpl.builder().withName(tuid("Helpdesk")).build());
        UserData user1 = userRepository.create(UserDataImpl.builder().withUsername(tuid("ChangeManagerUser")).build());
        UserData user2 = userRepository.create(UserDataImpl.builder().withUsername(tuid("HelpdeskUser")).build());
        roleRepository.setUserGroupsByName(user1.getId(), list(changeManager.getName()), changeManager.getName());
        roleRepository.setUserGroupsByName(user2.getId(), list(helpdesk.getName()), helpdesk.getName());
        List<GrantData> data = list();
        data.add(GrantDataImpl.builder()
                .withRoleId(changeManager.getId())
                .withMode(GM_WRITE)
                .withClassName(process.getName())
                .withType(POT_CLASS)
                .build());
        repository.setGrantsForRole(changeManager.getId(), data);

        List<GrantData> data2 = list();
        data2.add(GrantDataImpl.builder()
                .withRoleId(helpdesk.getId())
                .withMode(GM_WRITE)
                .withClassName(process.getName())
                .withType(POT_CLASS)
                .build());
        repository.setGrantsForRole(helpdesk.getId(), data2);

        workflowService.addXpdl(process.getName(),
                currentGroup.replaceAll("CurrentGroupProcess", process.getName())
                        .replaceAll("ChangeManager", changeManager.getName())
                        .replaceAll("Helpdesk", helpdesk.getName()));

        sessionService.impersonate(user1.getUsername());
        Long id = workflowService.startProcess(process.getName(), map(), true).getFlowCard().getId();
        assertEquals(changeManager.getName(), ((ReferenceType) workflowService.getAllFlowData(process.getName(), id).get("_CurrentGroup")).getDescription());

        sessionService.impersonate(user2.getUsername());
        Long id2 = workflowService.startProcess(process.getName(), map(), true).getFlowCard().getId();
        assertEquals(helpdesk.getName(), ((ReferenceType) workflowService.getAllFlowData(process.getName(), id2).get("_CurrentGroup")).getDescription());
        workflowService.updateProcessWithOnlyTask(process.getName(), id2, map(), true);
        assertEquals(changeManager.getName(), ((ReferenceType) workflowService.getAllFlowData(process.getName(), id2).get("_CurrentGroup")).getDescription());
    }

    @Test
    public void testXpdlCacheDrop() {
        Classe process = dao.createClass(ClassDefinitionImpl.builder()
                .withName(tuid("NewTestProcess"))
                .withParent(dao.getClasse("Activity"))
                .build());
        workflowService.addXpdl(process.getName(), xpdl_v1.replaceAll("NewTestProcess", process.getName()));

        TaskDefinition task_v1 = workflowService.getEntryTaskForCurrentUser(process.getName());

        List<TaskAttribute> variables_v1 = task_v1.getVariables();
        assertEquals(1, variables_v1.size());
        assertEquals("Description", getOnlyElement(variables_v1).getName());

        workflowService.addXpdl(process.getName(), xpdl_v2.replaceAll("NewTestProcess", process.getName()));

        TaskDefinition task_v2 = workflowService.getEntryTaskForCurrentUser(process.getName());

        List<TaskAttribute> variables_v2 = task_v2.getVariables();
        assertEquals(2, variables_v2.size());
        assertEquals(set("Code", "Description"), set(transform(variables_v2, TaskAttribute::getName)));
    }

    @Test
    public void testRfcParallelActivities() {
        Classe process = dao.createClass(ClassDefinitionImpl.builder().withName(tuid("RfcTest")).withParent(dao.getClasse("Activity")).build());
        workflowService.addXpdl(process.getName(), rfc.replaceAll("RequestForChange", process.getName()));

        Flow card = workflowService.startProcess(process.getName(), emptyMap(), true).getFlowCard();

        Task task = getOnlyElement(workflowService.getTaskList(card));
        assertEquals("FormalEvaluation_user", task.getDefinition().getId());

        card = workflowService.updateProcess(process.getName(), card.getCardId(), task.getId(), map(
                "FormalEvaluation", true,
                "ImpactAnalysisRequest", true,
                "CostAnalysisRequest", true
        ), true).getFlowCard();

        assertEquals(set("RFCImpactAnalysis_user", "RFCCostAnalysis_user"), workflowService.getTaskList(card).stream().map(Task::getDefinition).map(TaskDefinition::getId).collect(toSet()));

        task = workflowService.getTaskByDefinitionId(card, "RFCImpactAnalysis_user");
        card = workflowService.updateProcess(process.getName(), card.getCardId(), task.getId(), map(), true).getFlowCard();

        assertEquals(set("RFCCostAnalysis_user"), workflowService.getTaskList(card).stream().map(Task::getDefinition).map(TaskDefinition::getId).collect(toSet()));

        task = workflowService.getTaskByDefinitionId(card, "RFCCostAnalysis_user");
        card = workflowService.updateProcess(process.getName(), card.getCardId(), task.getId(), map(), true).getFlowCard();

        assertEquals(set("Decision_user"), workflowService.getTaskList(card).stream().map(Task::getDefinition).map(TaskDefinition::getId).collect(toSet()));
    }

    @After
    public void cleanup() {
        sessionService.deleteCurrentSessionIfExists();
    }
}
