package org.cmdbuild.dao.query.clause.alias;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;

public class EntryTypeAlias extends AbstractAlias {

	private final EntryType entryType;
	private final String alias;

	private EntryTypeAlias(EntryType entryType) {
		this.entryType = checkNotNull(entryType);

		String name;
		if (entryType instanceof Domain) {
			name = "Map_" + entryType.getName();
		} else {
			name = entryType.getName();
		}

//		if (isBlank(entryType.getSchema())) {
		alias = name;
//		} else {
//			alias = format("%s_%s", entryType.getSchema(), name);
//		}
	}

	@Override
	public void accept(AliasVisitor visitor) {
		visitor.visit(this);
	}

	public EntryType getEntryType() {
		return entryType;
	}

	@Override
	public String asString() {
		return alias;
	}

	public static EntryTypeAlias canonicalAlias(EntryType entryType) {
		return new EntryTypeAlias(entryType);
	}

}
