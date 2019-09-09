/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.serializationhelpers;

import static com.google.common.collect.FluentIterable.from;
import java.util.function.BiConsumer;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;

public class ReferenceAttributesSerializerv2 {

    public static void expandReferenceAttributes(Card card, BiConsumer<String, Object> newAttrConsumer) {
        for (Attribute referenceAttribute : from(card.getType().getAllAttributes()).filter((a) -> a.isOfType(REFERENCE))) {
            Object value = card.get(referenceAttribute.getName());
            if (value instanceof IdAndDescriptionImpl) {
                IdAndDescriptionImpl idAndDescriptionValue = (IdAndDescriptionImpl) value;
                if (idAndDescriptionValue.getId() != null) {
                    newAttrConsumer.accept("_" + referenceAttribute.getName() + "_description", idAndDescriptionValue.getDescription());
                }
            }
        }
    }
}
