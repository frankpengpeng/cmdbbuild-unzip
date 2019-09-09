package org.cmdbuild.test.web.utils;

import java.util.ArrayList;
import java.util.List;

//TODO use and return in textmetrics
//TODO build some mechanism for simple (auto), not verbose usage

@Deprecated // don't uese and remove soon
public class UITestMetrics {
	

	public static class Action {

		public Action() {
		}

		public Action(String description, long executionTime) {
			this.description = description;
			this.executionTime = executionTime;
		}

		public long executionTime;
		public String description;

	}

	private long executionTime;
	private List<Action> actions = new ArrayList<>();

	public void addAction(Action action) {
		actions.add(action);
		executionTime += action.executionTime;
	}

}
