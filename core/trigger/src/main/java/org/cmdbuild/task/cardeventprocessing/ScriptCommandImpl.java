package org.cmdbuild.task.cardeventprocessing;

import static org.apache.commons.lang3.StringUtils.abbreviate;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ScriptCommandImpl implements ScriptCommand {

	private final String engine, script, id;

	private ScriptCommandImpl(ScriptCommandImplBuilder builder) {
		this.engine = checkNotBlank(builder.engine);
		this.script = checkNotBlank(builder.script);
		this.id = checkNotBlank(builder.id);
	}

	@Override
	public String getEngine() {
		return engine;
	}

	@Override
	public String getScript() {
		return script;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "ScriptCommandImpl{" + "id=" + id + ", engine=" + engine + ", script=" + abbreviate(script, 20) + '}';
	}

	public static ScriptCommandImplBuilder builder() {
		return new ScriptCommandImplBuilder();
	}

	public static ScriptCommandImplBuilder copyOf(ScriptCommandImpl source) {
		return new ScriptCommandImplBuilder()
				.withEngine(source.getEngine())
				.withScript(source.getScript())
				.withId(source.getId());
	}

	public static class ScriptCommandImplBuilder implements Builder<ScriptCommandImpl, ScriptCommandImplBuilder> {

		private String engine;
		private String script;
		private String id;

		public ScriptCommandImplBuilder withEngine(String engine) {
			this.engine = engine;
			return this;
		}

		public ScriptCommandImplBuilder withScript(String script) {
			this.script = script;
			return this;
		}

		public ScriptCommandImplBuilder withId(String id) {
			this.id = id;
			return this;
		}

		@Override
		public ScriptCommandImpl build() {
			return new ScriptCommandImpl(this);
		}

	}
}
