/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import com.google.common.collect.Iterables;
import java.util.List;

public interface AttributeFilter {

	AttributeFilterMode getMode();

	List<AttributeFilter> getElements();

	AttributeFilterCondition getCondition();

	default boolean isSimple() {
		return AttributeFilterMode.SIMPLE.equals(getMode());
	}

	default AttributeFilter getOnlyElement() {
		return Iterables.getOnlyElement(getElements());
	}

	default boolean hasOnlyElement() {
		return getElements().size() == 1;
	}

	enum AttributeFilterMode {
		SIMPLE, AND, OR, NOT
	}

}
