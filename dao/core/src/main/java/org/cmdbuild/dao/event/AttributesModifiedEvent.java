/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.event;

import java.util.List;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.EntryType;

public interface AttributesModifiedEvent extends DaoEvent {

    List<AttributeWithoutOwner> getAttributes();

    EntryType getOwner();
}
