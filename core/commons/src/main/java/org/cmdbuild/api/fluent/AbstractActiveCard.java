package org.cmdbuild.api.fluent;

abstract class AbstractActiveCard extends CardImpl {

	private final FluentApiExecutor executor;

	AbstractActiveCard(final FluentApiExecutor executor, final String className, final Long id) {
		super(className, id);
		this.executor = executor;
	}

	protected FluentApiExecutor executor() {
		return executor;
	}

}
