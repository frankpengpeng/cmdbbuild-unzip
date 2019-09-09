/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.dao.function;

import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public interface StoredFunctionParameter {

	String getName();

	CardAttributeType<?> getType();

}
