package org.cmdbuild.dao.entrytype;

import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public interface Attribute extends AttributeWithoutOwner, EntryTypeOrAttribute {

	EntryType getOwner();

	default boolean isOfType(AttributeTypeName... types) {
		return set(types).contains(getType().getName());
	}

	default Classe getOwnerClass() {
		return (Classe) getOwner();
	}

}
