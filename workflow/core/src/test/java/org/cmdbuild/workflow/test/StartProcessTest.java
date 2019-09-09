package org.cmdbuild.workflow.test;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Task;
import org.cmdbuild.task.startworkflow.StartProcessAction;
import org.cmdbuild.task.startworkflow.StartProcessAction.Builder;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.model.AdvancedFlowStatus;
import org.cmdbuild.workflow.model.SimpleFlowAdvanceResponse;
import org.cmdbuild.workflow.model.Flow;

@RunWith(MockitoJUnitRunner.class)
public class StartProcessTest {

	@Captor
	private ArgumentCaptor<Map<String, Object>> attributesCaptor;

	@Captor
	private ArgumentCaptor<Map<String, Object>> widgetSubmissionCaptor;

	@Test(expected = NullPointerException.class)
	public void workflowLogicIsRequired() throws Exception {
		// given
		final Builder builder = StartProcessAction.newInstance() //
				.withClassName("foo");

		// when
		builder.build();
	}

	@Test(expected = NullPointerException.class)
	public void classNameIsRequired() throws Exception {
		// given
		final WorkflowService workflowLogic = mock(WorkflowService.class);
		final Builder builder = StartProcessAction.newInstance() //
				.withWorkflowLogic(workflowLogic);

		// when
		builder.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void classNameMustBeNotBlank() throws Exception {
		// given
		final WorkflowService workflowLogic = mock(WorkflowService.class);
		final Builder builder = StartProcessAction.newInstance() //
				.withWorkflowLogic(workflowLogic) //
				.withClassName(" \t");

		// when
		builder.build();
	}

	@Test
	public void templateResolverIsNotRequired() throws Exception {
		// given
		final WorkflowService workflowLogic = mock(WorkflowService.class);
		final Builder builder = StartProcessAction.newInstance() //
				.withWorkflowLogic(workflowLogic) //
				.withClassName("foo");

		// when
		builder.build();
	}

//	@Test TODO restore tests
//	public void processStartedWithoutAttributes() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		final Map<String, Object> attributes = attributesCaptor.getValue();
//		assertThat(attributes.isEmpty(), equalTo(true));
//
//		final Map<String, Object> widgetSubmission = widgetSubmissionCaptor.getValue();
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//	}
//
//	@Test
//	public void processStartedWithAttributes() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAttribute("bar", "BAR") //
//				.withAttribute("baz", 42L) //
//				.withAttributes(new HashMap<String, Object>() {
//					{
//						put("a", "A");
//						put("b", 123);
//					}
//				}) //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		final Map<String, Object> attributes = attributesCaptor.getAllValues().get(0);
//		assertThat(attributes.size(), equalTo(4));
//		assertThat(attributes, hasEntry("bar", (Object) "BAR"));
//		assertThat(attributes, hasEntry("baz", (Object) 42L));
//		assertThat(attributes, hasEntry("a", (Object) "A"));
//		assertThat(attributes, hasEntry("b", (Object) 123));
//
//		final Map<String, Object> widgetSubmission = widgetSubmissionCaptor.getAllValues().get(0);
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//	}
//
//	@Test
//	public void settingAttributeWithNullNameDoesNothing() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAttribute(null, "bar") //
//				.withAttribute("baz", 42L) //
//				.withAttributes(new HashMap<String, Object>() {
//					{
//						put("bar", "BAR");
//					}
//				}) //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		final Map<String, Object> attributes = attributesCaptor.getAllValues().get(0);
//		assertThat(attributes.size(), equalTo(2));
//		assertThat(attributes, hasEntry("bar", (Object) "BAR"));
//		assertThat(attributes, hasEntry("baz", (Object) 42L));
//
//		final Map<String, Object> widgetSubmission = widgetSubmissionCaptor.getAllValues().get(0);
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//	}
//
//	@Test
//	public void settingAttributeWithBlankNameDoesNothing() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAttribute(" \t", "should not be added") //
//				.withAttribute("baz", 42L) //
//				.withAttributes(new HashMap<String, Object>() {
//					{
//						put("bar", "BAR");
//						put("\t ", "should not be added");
//					}
//				}) //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		Map<String, Object> attributes = attributesCaptor.getAllValues().get(0);
//		assertThat(attributes.size(), equalTo(2));
//		assertThat(attributes, hasEntry("bar", (Object) "BAR"));
//		assertThat(attributes, hasEntry("baz", (Object) 42L));
//		attributes = attributesCaptor.getAllValues().get(1);
//		assertThat(attributes.isEmpty(), equalTo(true));
//
//		Map<String, Object> widgetSubmission = widgetSubmissionCaptor.getAllValues().get(0);
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//		widgetSubmission = widgetSubmissionCaptor.getAllValues().get(1);
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//	}
//
//	@Test
//	public void settingNullAttributesDoesNothing() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAttributes(null) //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		final Map<String, Object> attributes = attributesCaptor.getAllValues().get(0);
//		assertThat(attributes.size(), equalTo(0));
//
//		final Map<String, Object> widgetSubmission = widgetSubmissionCaptor.getAllValues().get(0);
//		assertThat(widgetSubmission.isEmpty(), equalTo(true));
//	}
//
//	@Test
//	public void processIsAdvanceableImplicitly() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//	}
//
//	@Test
//	public void processAdvanceableOrNot() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//		final StartProcessAction advanceable = StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAdvanceStatus(true) //
//				.build();
//		final StartProcessAction nonAdvanceable = StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withClassName("foo") //
//				.withAdvanceStatus(false) //
//				.build();
//
//		// when
//		advanceable.execute();
//		nonAdvanceable.execute();
//
//		// verify
//		final InOrder inOrder = inOrder(workflowLogic);
//		inOrder.verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(false));
//		inOrder.verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//		inOrder.verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(false));
//	}
//
//	@Test
//	public void whenSpecifiedTemplateResolverIsInvokedForEveryParameter() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//		final EasytemplateProcessor templateResolver = mock(EasytemplateProcessor.class);
//		when(templateResolver.resolve(anyString())) //
//				.thenAnswer(new Answer<String>() {
//
//					@Override
//					public String answer(final InvocationOnMock invocation) throws Throwable {
//						final Object raw = invocation.getArguments()[0];
//						final String template = String.class.cast(raw);
//						return StringUtils.reverse(template);
//					}
//
//				});
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withTemplateResolver(templateResolver) //
//				.withClassName("foo") //
//				.withAttribute("foo", "FOO") //
//				.withAttribute("bar", "BAR") //
//				.withAttribute("baz", "BAZ") //
//				.build() //
//				.execute();
//
//		// verify
//		verify(templateResolver).resolve("FOO");
//		verify(templateResolver).resolve("BAR");
//		verify(templateResolver).resolve("BAZ");
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//
//		final Map<String, Object> attributes = attributesCaptor.getAllValues().get(0);
//		assertThat(attributes, hasEntry("foo", (Object) "OOF"));
//		assertThat(attributes, hasEntry("bar", (Object) "RAB"));
//		assertThat(attributes, hasEntry("baz", (Object) "ZAB"));
//	}
//
//	@Test
//	public void hookInvokedWhenProcessInstanceHasBeenCreated() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//		final StartProcessActionCallback hook = mock(StartProcessActionCallback.class);
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withHook(hook) //
//				.withClassName("foo") //
//				.withAdvanceStatus(false) //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(hook).created(created);
//		verifyNoMoreInteractions(workflowLogic, hook);
//	}
//
//	@Test
//	public void hooksInvokedWhenProcessInstanceHasBeenCreatedAndAdvanced() throws Exception {
//		// given
//		final WorkflowService workflowLogic = mock(WorkflowService.class);
//		final FlowCard created = mock(FlowCard.class);
//		doReturn(42L) //
//				.when(created).getCardId();
//		doReturn(asList(userActivityInstance("first"))) //
//				.when(created).getTaskList();
//		doReturn(response(created)) //
//				.when(workflowLogic).startProcess(anyString(), anyMap(), anyMap(), anyBoolean());
//		final FlowCard advanced = mock(FlowCard.class);
//		doReturn(response(advanced)) //
//				.when(workflowLogic).updateProcess(anyString(), anyLong(), anyString(), anyMap(), anyMap(),
//				anyBoolean());
//		final StartProcessActionCallback hook = mock(StartProcessActionCallback.class);
//
//		// when
//		StartProcessAction.newInstance() //
//				.withWorkflowLogic(workflowLogic) //
//				.withHook(hook) //
//				.withClassName("foo") //
//				.build() //
//				.execute();
//
//		// verify
//		verify(workflowLogic).startProcess(eq("foo"), attributesCaptor.capture(), widgetSubmissionCaptor.capture(),
//				eq(false));
//		verify(workflowLogic).updateProcess(eq("foo"), eq(42L), eq("first"), attributesCaptor.capture(),
//				widgetSubmissionCaptor.capture(), eq(true));
//		verify(hook).created(created);
//		verify(hook).advanced(advanced);
//	}

	/*
	 * Utilities
	 */
	private Task userActivityInstance(final String id) {
		final Task output = mock(Task.class, id);
		doReturn(id) //
				.when(output).getId();
		return output;
	}

	private FlowAdvanceResponse response(Flow card) {
		return SimpleFlowAdvanceResponse.builder()
				.withFlowCard(card)
				.withTasklist(emptyList())
				.withAdvancedFlowStatus(AdvancedFlowStatus.PROCESSING_SCRIPT)
				.build();
	}

}
