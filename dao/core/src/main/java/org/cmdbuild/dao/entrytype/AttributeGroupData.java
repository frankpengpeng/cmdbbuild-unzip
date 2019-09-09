/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import org.cmdbuild.dao.entrytype.EntryType.EntryTypeType;

public interface AttributeGroupData extends AttributeGroupInfo {

    int getIndex();

    String getOwnerName();

    EntryTypeType getOwnerType();
}
