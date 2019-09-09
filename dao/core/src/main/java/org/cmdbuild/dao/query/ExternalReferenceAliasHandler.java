package org.cmdbuild.dao.query;

import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;

public class ExternalReferenceAliasHandler {

	public static final String EXTERNAL_ATTRIBUTE = Constants.DESCRIPTION_ATTRIBUTE;

	private final String entryTypeAlias;
	private final String attributeName;

	public ExternalReferenceAliasHandler(final EntryType entryType, final Attribute attribute) {
		this(entryType.getName(), attribute);
	}

	public ExternalReferenceAliasHandler(final String entryTypeAlias, final Attribute attribute) {
		this.entryTypeAlias = entryTypeAlias;
		this.attributeName = attribute.getName();
	}

	public ExternalReferenceAliasHandler(final String entryTypeAlias, final String attributeName) {
		this.entryTypeAlias = entryTypeAlias;
		this.attributeName = attributeName;
	}

	public String buildTableAlias() {
		return String.format("%s#%s", entryTypeAlias, attributeName);
	}

	public String buildColumnAlias() {
		return String.format("%s#%s#%s", entryTypeAlias, attributeName, EXTERNAL_ATTRIBUTE);
	}

}
