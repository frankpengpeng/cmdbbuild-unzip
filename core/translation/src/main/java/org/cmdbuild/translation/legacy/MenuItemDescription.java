package org.cmdbuild.translation.legacy;

import java.util.Map;

import org.apache.commons.lang3.Validate;

public class MenuItemDescription extends BaseTranslation {

	private MenuItemDescription(final Builder builder) {
		this.setName(builder.uuid);
		this.setTranslations(builder.translations);
	}

	public static Builder newInstance() {
		return new Builder();
	}

	@Override
	public void accept(final TranslationObjectVisitor visitor) {
		visitor.visit(this);
	}

	public static class Builder implements org.apache.commons.lang3.builder.Builder<MenuItemDescription> {

		private String uuid;
		private Map<String, String> translations;

		private Builder() {
		}

		@Override
		public MenuItemDescription build() {
			validate();
			return new MenuItemDescription(this);
		}

		public Builder withUuid(final String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Builder withTranslations(final Map<String, String> translations) {
			this.translations = translations;
			return this;
		}

		private void validate() {
			Validate.notBlank(uuid);
		}

	}

}
