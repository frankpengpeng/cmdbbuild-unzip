package org.cmdbuild.dao.query.clause.from;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.dao.query.clause.ClassHistory;
import org.cmdbuild.dao.query.clause.alias.Alias;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.view.DataView;

public class ClassFromClause implements FromClause {

	private final DataView dataView;
	private final EntryType entryType;
	private final Alias alias;

	public ClassFromClause(DataView dataView, EntryType entryType, Alias alias) {
		Validate.isTrue(entryType instanceof Classe, "from clause must be for classes only, found entryType = %s", entryType);
		this.dataView = dataView;
		this.entryType = entryType;
		this.alias = alias;
	}

	@Override
	public EntryType getType() {
		return entryType;
	}

	@Override
	public Alias getAlias() {
		return alias;
	}

	@Override
	public boolean isHistory() {
		return entryType instanceof ClassHistory;
	}

	@Override
	public EntryTypeStatus getStatus(EntryType entryType) {
		return new EntryTypeStatus() {

			@Override
			public boolean isAccessible() {
				return dataView.getClassOrNull(entryType.getId()) != null;
			}

			@Override
			public boolean isActive() {
				return entryType.isActive();
			}

		};
	}

}
