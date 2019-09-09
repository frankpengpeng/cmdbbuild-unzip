/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import org.apache.commons.lang3.Validate;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public class StoredFunctionParameterImpl implements StoredFunctionParameter {

	private final String name;
	private final CardAttributeType<?> type;

	public StoredFunctionParameterImpl(String name, CardAttributeType<?> type) {
		Validate.notEmpty(name);
		Validate.notNull(type);
		this.name = name;
		this.type = type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public CardAttributeType<?> getType() {
		return type;
	}

}
