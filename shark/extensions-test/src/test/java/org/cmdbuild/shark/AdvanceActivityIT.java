package org.cmdbuild.shark;

import org.cmdbuild.workflow.model.WorkflowException;
import org.cmdbuild.workflow.shark.xpdl.XpdlProcess;
import org.junit.Test;

import org.cmdbuild.shark.test.utils.AbstractLocalSharkServiceTest;
import org.junit.Ignore;

public class AdvanceActivityIT extends AbstractLocalSharkServiceTest {

//	private static class ExtendedLocalSharkService extends LocalSharkService {
//
//		public ExtendedLocalSharkService(final Config config) {
//			super(config);
//		}
//
//		public void runButDontCloseActivityInstance(final String procInstId, final String actInstId)
//				throws WorkflowException {
//			new TransactedExecutor<Void>() {
//				@Override
//				protected Void command() throws WorkflowException {
//					try {
//						wapi().changeActivityInstanceState(handle(), procInstId, actInstId,
//								WMActivityInstanceState.OPEN_RUNNING);
//					} catch (final Exception e) {
//						throw new WorkflowException(e);
//					}
//					return null;
//				}
//			}.execute();
//		}
//	}
//
//	private static ExtendedLocalSharkService extendedWs;
//
//	@Before
//	public void initWorkflowService() {
//		extendedWs = new ExtendedLocalSharkService(new LocalSharkService.Config() {
//			@Override
//			public String getUsername() {
//				return AbstractLocalSharkServiceTest.USERNAME;
//			}
//		});
//		sharkService = extendedWs;
//	}
	private XpdlProcess process;

	@Test
	@Ignore
	public void activitiesStillRunningCanBeAdvanced() throws Exception {
//		process = xpdlDocument.createProcess(randomName());
//		process.createTransition( //
//				process.createActivity("A"), //
//				process.createActivity("B") //
//		);
//		final String procInstId = uploadXpdlAndStartProcess(process).getWalkId();
//		final String actInstId = firstAndOnlyActivityInstanceId(procInstId);
//
//		extendedWs.runButDontCloseActivityInstance(procInstId, actInstId);
//		sharkService.advanceActivityInstance(procInstId, actInstId);
	}

//	private String firstAndOnlyActivityInstanceId(final String procInstId) throws WorkflowException {
//		final TaskInfo[] activities = sharkService.findOpenActivitiesForProcessInstance(procInstId);
//		assertThat(activities.length, is(1));
//		return activities[0].getActivityInstanceId();
//	}

}
