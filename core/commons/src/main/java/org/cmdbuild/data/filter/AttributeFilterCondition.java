/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;

public interface AttributeFilterCondition {

	ConditionOperator getOperator();

	String getKey();

	List<String> getValues();

	default boolean hasSingleValue() {
		return getValues().size() == 1;
	}

	default String getSingleValue() {
		return getOnlyElement(getValues());
	}

	public String getClassName();

	boolean hasClassName();

	enum ConditionOperator {
		EQUAL,
		NOTEQUAL,
		ISNULL,
		ISNOTNULL,
		GREATER,
		LESS,
		BETWEEN,
		LIKE,
		CONTAIN,
		NOTCONTAIN,
		BEGIN,
		NOTBEGIN,
		END,
		NOTEND,
		IN,
		NET_CONTAINED,
		NET_CONTAINEDOREQUAL,
		NET_CONTAINS,
		NET_CONTAINSOREQUAL,
		NET_RELATION
	}

}
