/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.dao.beans.LookupValue;
import static org.cmdbuild.dao.beans.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.service.rest.common.serializationhelpers.CardWsSerializationHelper.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.common.utils.WsAttributeConverterUtils.toClient;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;

@Component
public class CardWsSerializationHelper {

    private final List<String> CARD_ATTR_EXT_META = ImmutableList.of("_changed", "_previous");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectTranslationService translationService;
    private final ClassSerializationHelper classSerializationHelper;
    private final AttributeTypeConversionService attributeTypeConversionService;

    public CardWsSerializationHelper(ObjectTranslationService translationService, ClassSerializationHelper classSerializationHelper, AttributeTypeConversionService attributeTypeConversionService) {
        this.translationService = checkNotNull(translationService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
    }

    public enum ExtendedCardOptions {
        NONE, INCLUDE_MODEL
    }

    public CmMapUtils.FluentMap<String, Object> serializeMinimalRelation(CMRelation relation) {
        return map("_id", relation.getId(),
                "_type", relation.getType().getName(),
                "_user", relation.getUser(),
                "_beginDate", toIsoDateTime(relation.getBeginDate()),
                "_sourceType", relation.getSourceCard().getClassName(),
                "_sourceId", relation.getSourceId(),
                "_sourceDescription", relation.getSourceDescription(),//TODO translation
                "_sourceCode", relation.getSourceCode(),
                "_destinationType", relation.getTargetCard().getClassName(),
                "_destinationId", relation.getTargetId(),
                "_destinationCode", relation.getTargetCode(),
                "_destinationDescription", relation.getTargetDescription(),//TODO translation
                "_direction", serializeRelationDirection(relation.getDirection()),
                "_is_direct", relation.isDirect(),
                "_can_update", relation.getType().hasServicePermission(CP_UPDATE),
                "_can_delete", relation.getType().hasServicePermission(CP_DELETE)
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedRelation(CMRelation relation) {
        return serializeMinimalRelation(relation).accept((map) -> {
            addCardValuesAndDescriptionsAndExtras(relation, map::put);
        });
    }

    public FluentMap<Object, Object> serializeCard(Card card, ExtendedCardOptions... extendedCardOptions) {
        try {
            return map("_id", card.getId(),
                    "_type", card.getType().getName(),
                    "_user", card.getUser(),
                    "_beginDate", CmDateUtils.toIsoDateTime(card.getBeginDate())
            ).accept((m) -> {
                if (card.getType().hasMultitenantEnabled()) {
                    m.put("_tenant", card.getTenantId());
                }
                addCardValuesAndDescriptionsAndExtras(card, m::put);
                if (set(extendedCardOptions).contains(INCLUDE_MODEL)) {
                    m.put("_model", classSerializationHelper.buildBasicResponse(card.getType())
                            .with("attributes", list(transform(card.getType().getServiceAttributes(), attributeTypeConversionService::serializeAttributeType))));
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error serializing card = %s", card);
        }
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card.getType().getServiceAttributes(), card::get, adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(Collection<Attribute> attributes, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        attributes.stream().filter(Attribute::hasServiceReadPermission).forEach((a) -> {
            addCardValuesAndDescriptionsAndExtras(a.getName(), a.getType(), getter, adder);
        });
    }

    public FluentMap<Object, Object> serializeAttributeValue(EntryType classe, String attributeName, Map<String, Object> data) {
        return map().accept(m -> {
            Attribute attribute = classe.getAttribute(attributeName);
            addCardValuesAndDescriptionsAndExtras(attribute.getName(), attribute.getType(), data::get, m::put);
        });
    }

    public void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType type, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        Object rawValue = "<undefined>";
        try {
            rawValue = getter.apply(name);
            Object value = toClient(type, rawValue);
            adder.accept(name, value);
            switch (type.getName()) {
                case REFERENCE:
                case FOREIGNKEY:
                case LOOKUP:
                    if (rawValue instanceof IdAndDescription) {
                        adder.accept(format("_%s_code", name), ((IdAndDescription) rawValue).getCode());
                        adder.accept(format("_%s_description", name), ((IdAndDescription) rawValue).getDescription());
                    }
                    if (rawValue instanceof LookupValue) {
                        LookupValue lookup = ((LookupValue) rawValue);
                        adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(lookup.getLookupType(), lookup.getCode(), lookup.getDescription()));
                    }
            }
            CARD_ATTR_EXT_META.stream().map((e) -> format("_%s%s", name, e)).forEach((n) -> {
                Object v = getter.apply(n);
                if (v != null) {
                    adder.accept(n, v);
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error processing attr = %s of type = %s with value = %s", name, type, rawValue);
        }
    }

}
