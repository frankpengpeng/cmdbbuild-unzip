package org.cmdbuild.workflow.model;

import java.util.List;
import org.cmdbuild.widget.model.Widget;

public interface Task {

	String getId();

	String getFlowId();
	
	Flow getProcessInstance();

	TaskDefinition getDefinition();

	String getPerformerName();

	/**
	 * Returns the activity widgets for this process instance, with expansion of
	 * "server" variables.
	 *
	 * @return ordered list of widgets for this activity instance
	 * @
	 */
	List<Widget> getWidgets();

	/**
	 * The current user can modify this activity instance.
	 *
	 * @return if it is modifiable
	 */
	boolean isWritable();

}
