package org.cmdbuild.lookup;

import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class LookupTypeImpl implements LookupType {

	private final String name;
	private final String parent;

	private LookupTypeImpl(LookupTypeImplBuilder builder) {
		this.name = checkNotBlank(builder.name);
		this.parent = builder.parent;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public @Nullable
	String getParentOrNull() {
		return parent;
	}

	public static LookupTypeImplBuilder builder() {
		return new LookupTypeImplBuilder();
	}

	public static LookupTypeImplBuilder copyOf(LookupTypeImpl source) {
		return new LookupTypeImplBuilder()
				.withName(source.getName())
				.withParent(source.getParentOrNull());
	}

	public static class LookupTypeImplBuilder implements Builder<LookupTypeImpl, LookupTypeImplBuilder> {

		private String name;
		private String parent;

		public LookupTypeImplBuilder withName(String name) {
			this.name = name;
			return this;
		}

		public LookupTypeImplBuilder withParent(String parent) {
			this.parent = parent;
			return this;
		}

		@Override
		public LookupTypeImpl build() {
			return new LookupTypeImpl(this);
		}

	}
}
