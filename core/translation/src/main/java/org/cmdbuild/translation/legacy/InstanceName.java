package org.cmdbuild.translation.legacy;

import java.util.Map;


public class InstanceName extends BaseTranslation {

	private InstanceName(final Builder builder) {
		this.setTranslations(builder.translations);
	}

	public static Builder newInstance() {
		return new Builder();
	}

	@Override
	public void accept(final TranslationObjectVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<InstanceName> {

		private Map<String, String> translations;

		private Builder() {
		}

		@Override
		public InstanceName build() {
			return new InstanceName(this);
		}

		public Builder withTranslations(final Map<String, String> translations) {
			this.translations = translations;
			return this;
		}

	}

}