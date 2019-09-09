/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.event;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.singletonList;
import java.util.List;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.EntryType;

public class AttributesCreatedEventImpl implements AttributesModifiedEvent {

    private final List<AttributeWithoutOwner> attributes;
    private final EntryType owner;

    public AttributesCreatedEventImpl(AttributeWithoutOwner attribute, EntryType owner) {
        this(singletonList(attribute), owner);
    }

    public AttributesCreatedEventImpl(List<AttributeWithoutOwner> attributes, EntryType owner) {
        this.attributes = ImmutableList.copyOf(attributes);
        this.owner = checkNotNull(owner);
    }

    @Override
    public List<AttributeWithoutOwner> getAttributes() {
        return attributes;
    }

    @Override
    public EntryType getOwner() {
        return owner;
    }

//    @Override
//    public EntryTypeOrAttribute getCreatedItem() {
//        return AttributeImpl.copyOf(attribute).withOwner(owner).build();
//    }
}
