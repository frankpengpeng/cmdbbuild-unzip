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

public class AttributeModifiedEventImpl implements AttributesModifiedEvent {

    private final List<AttributeWithoutOwner> attributes;
    private final EntryType owner;

    public AttributeModifiedEventImpl(AttributeWithoutOwner attribute, EntryType owner) {
        this(singletonList(attribute), owner);
    }

    public AttributeModifiedEventImpl(List<AttributeWithoutOwner> attributes, EntryType owner) {
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

}
