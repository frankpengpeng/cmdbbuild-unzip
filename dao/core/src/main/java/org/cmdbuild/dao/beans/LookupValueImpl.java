package org.cmdbuild.dao.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;

public class LookupValueImpl extends IdAndDescriptionImpl implements LookupValue {

	private final String lookupType;

	public LookupValueImpl(@Nullable Long id, @Nullable String description, @Nullable String lookupType) {
		super(id, description);
		this.lookupType = lookupType;
	}

	private LookupValueImpl(LookupValueBuilder builder) {
		super(builder.id, builder.description, builder.code);
		this.lookupType = checkNotNull(builder.lookupType);
	}

	@Override
	public String getLookupType() {
		return lookupType;
	}

	@Override
	public String toString() {
		return "LookupValueImpl{" + "id=" + getId() + ", description=" + getDescription() + '}';
	}

	public static LookupValueBuilder builder() {
		return new LookupValueBuilder();
	}

	public static LookupValueBuilder copyOf(LookupValue source) {
		return new LookupValueBuilder()
				.withId(source.getId())
				.withDescription(source.getDescription())
				.withLookupType(source.getLookupType());
	}

	public static class LookupValueBuilder implements Builder<LookupValueImpl, LookupValueBuilder> {

		private Long id;
		private String description;
		private String code;
		private String lookupType;

		public LookupValueBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public LookupValueBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public LookupValueBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public LookupValueBuilder withLookupType(String lookupType) {
			this.lookupType = lookupType;
			return this;
		}

		@Override
		public LookupValueImpl build() {
			return new LookupValueImpl(this);
		}

	}
}
