/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.AttributeImpl;
import org.cmdbuild.dao.entrytype.ClassDefinitionImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.test.framework.CmTestRunner;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.prepareTuid;
import static org.cmdbuild.test.framework.UniqueTestIdUtils.tuid;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.core.fluentapi.WorkflowApiService;
import static org.cmdbuild.workflow.model.FlowStatus.ABORTED;
import static org.cmdbuild.workflow.model.FlowStatus.COMPLETED;
import static org.cmdbuild.workflow.model.FlowStatus.OPEN;
import static org.cmdbuild.workflow.model.FlowStatus.SUSPENDED;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(CmTestRunner.class)
public class WfApiProcessIT {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final WorkflowApiService apiService;
    private final GlobalConfigService configService;
    private final WorkflowService workflowService;
    private final SessionService sessionService;

    private final String xpdl_file = readToString(getClass().getResourceAsStream("/org/cmdbuild/test/core/simpletestprocess.xpdl"));

    private Classe process;

    public WfApiProcessIT(DaoService dao, SessionService sessionService, WorkflowService workflowService, GlobalConfigService configService, WorkflowApiService apiService) {
        this.dao = checkNotNull(dao);
        this.sessionService = checkNotNull(sessionService);
        this.workflowService = checkNotNull(workflowService);
        this.configService = checkNotNull(configService);
        this.apiService = checkNotNull(apiService);
    }

    @Before
    public void init() {
        prepareTuid();

        sessionService.createAndSet(LoginDataImpl.buildNoPasswordRequired("admin"));

        configService.putString("org.cmdbuild.workflow.enabled", "true");
        configService.putString("org.cmdbuild.sql.log.enabled", "true");

        process = dao.createClass(ClassDefinitionImpl.builder()
                .withName(tuid("simpletestprocess"))
                .withParent(dao.getClasse("Activity"))
                .build());
        dao.createAttribute(AttributeImpl.builder().withOwner(process).withName("MyAttr").withType(new StringAttributeType()).build());
        dao.createAttribute(AttributeImpl.builder().withOwner(process).withName("MyOtherAttr").withType(new StringAttributeType()).build());
        process = dao.getClasse(process);
        workflowService.addXpdl(process.getName(), newDataSource(xpdl_file.replace("simpletestprocess", process.getName())));
    }

    @Test
    public void testProcessSuspendResumeAbort() {
        FlowAdvanceResponse startedProcess = workflowService.startProcess(process.getName(), map(), true);
        apiService.getWorkflowApi().existingProcessInstance(process.getName(), startedProcess.getFlowCard().getId()).suspend();
        assertEquals(SUSPENDED, workflowService.getFlowCard(process.getName(), startedProcess.getFlowCard().getId()).getStatus());

        apiService.getWorkflowApi().existingProcessInstance(process.getName(), startedProcess.getFlowCard().getId()).resume();
        assertEquals(OPEN, workflowService.getFlowCard(process.getName(), startedProcess.getFlowCard().getId()).getStatus());

        apiService.getWorkflowApi().existingProcessInstance(process.getName(), startedProcess.getFlowCard().getId()).abort();
        assertEquals(ABORTED, workflowService.getFlowCard(process.getName(), startedProcess.getFlowCard().getId()).getStatus());
    }

    @Test
    public void testProcessStartAdvance() {
        long cardId = apiService.getWorkflowApi().newProcessInstance(process.getName())
                .withAttribute("MyAttr", "begin")
                .withAttribute("MyOtherAttr", "something")
                .startAndAdvance().getId();

        assertEquals(OPEN, workflowService.getFlowCard(process.getName(), cardId).getStatus());
        assertEquals("begin", workflowService.getFlowCard(process.getName(), cardId).get("MyAttr"));
        assertEquals("something", workflowService.getFlowCard(process.getName(), cardId).get("MyOtherAttr"));
        assertEquals("Step2_user", getOnlyElement(workflowService.getTaskListForCurrentUserByClassIdAndCardId(process.getName(), cardId)).getDefinition().getId());

        apiService.getWorkflowApi().existingProcessInstance(process.getName(), cardId)
                .withAttribute("MyAttr", "next")
                .withAttribute("MyOtherAttr", "qwerty")
                .advance();

        assertEquals(OPEN, workflowService.getFlowCard(process.getName(), cardId).getStatus());
        assertEquals("next", workflowService.getFlowCard(process.getName(), cardId).get("MyAttr"));
        assertEquals("qwerty", workflowService.getFlowCard(process.getName(), cardId).get("MyOtherAttr"));
        assertEquals("Step3_user", getOnlyElement(workflowService.getTaskListForCurrentUserByClassIdAndCardId(process.getName(), cardId)).getDefinition().getId());

        apiService.getWorkflowApi().existingProcessInstance(process.getName(), cardId)
                .withAttribute("MyAttr", "update")
                .withAttribute("MyOtherAttr", "aieie")
                .update();

        assertEquals(OPEN, workflowService.getFlowCard(process.getName(), cardId).getStatus());
        assertEquals("update", workflowService.getFlowCard(process.getName(), cardId).get("MyAttr"));
        assertEquals("aieie", workflowService.getFlowCard(process.getName(), cardId).get("MyOtherAttr"));
        assertEquals("Step3_user", getOnlyElement(workflowService.getTaskListForCurrentUserByClassIdAndCardId(process.getName(), cardId)).getDefinition().getId());

        apiService.getWorkflowApi().existingProcessInstance(process.getName(), cardId).advance();

        assertEquals(COMPLETED, workflowService.getFlowCard(process.getName(), cardId).getStatus());
    }
}
