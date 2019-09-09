/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.MoreObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.transformEntries;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.joda.time.DateTime;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;

public class CardImpl implements Card {

    private final Long tenantId, id, currentId;
    private final Classe type;
    private final String code, description, user;
    private final ZonedDateTime beginDate, endDate;
    private final CardStatus status;//TODO get status
    private final Map<String, Object> attributes;

    private CardImpl(CardImplBuilder builder) {
        this.type = checkNotNull(builder.type, "card type cannot be null");
        this.attributes = map(transformEntries(builder.rawAttributes, (String key, Object value) -> {
            Attribute attribute = type.getAttributeOrNull(key);
            if (attribute != null) {
                value = rawToSystem(attribute, value);
            }
            return value;
        })).immutable();
        this.id = convert(attributes.get(ATTR_ID), Long.class);
        this.currentId = firstNotNullOrNull(convert(attributes.get(ATTR_CURRENTID), Long.class), id);
        this.tenantId = convert(attributes.get(ATTR_IDTENANT), Long.class);
        code = nullToEmpty(convert(attributes.get(ATTR_CODE), String.class));
        description = nullToEmpty(convert(attributes.get(ATTR_DESCRIPTION), String.class));
        user = nullToEmpty(convert(attributes.get(ATTR_USER), String.class));
        beginDate = convert(attributes.get(ATTR_BEGINDATE), ZonedDateTime.class);
        endDate = convert(attributes.get(ATTR_ENDDATE), ZonedDateTime.class);
        switch (type.getClassType()) {
            case STANDARD:
                status = MoreObjects.firstNonNull(convert(attributes.get(ATTR_STATUS), CardStatus.class),
                        endDate == null ? CardStatus.A : CardStatus.U);//N ?
                break;
            case SIMPLE:
                status = CardStatus.A;
                break;
            default:
                throw unsupported("unsupported class type = %s", type.getClassType());
        }

    }

    @Override
    public Classe getType() {
        return type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Long getCurrentId() {
        return checkNotNull(currentId, "no id for this card (new card)");
    }

    @Override
    public Long getId() {
        return checkNotNull(id, "no id for this card (new card)");
    }

    @Override
    public boolean hasId() {
        return id != null;
    }

    @Override
    @Nullable
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public DateTime getBeginDate() {//TODO move to java 8 time
        return new DateTime(checkNotNull(beginDate, "no begin date for this card (new card)").toInstant().toEpochMilli());
    }

    @Override
    @Nullable
    public DateTime getEndDate() {//TODO move to java 8 time
        return endDate == null ? null : new DateTime(endDate.toInstant().toEpochMilli());
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getRawValues() {
        return attributes.entrySet();
    }

    @Override
    public Map<String, Object> getAllValuesAsMap() {
        return attributes;
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getAttributeValues() {
        return attributes.entrySet().stream().filter((e) -> type.hasAttribute(e.getKey()) && !type.getAttribute(e.getKey()).hasNotServiceListPermission()).collect(toList());
    }

    @Override
    public CardStatus getCardStatus() {
        return status;
    }

    public static CardImplBuilder builder() {
        return new CardImplBuilder();
    }

    public static CardImpl buildCard(Classe type, Map<String, Object> attributes) {
        return builder().withType(type).withAttributes(attributes).build();
    }

    public static CardImpl buildCard(Classe type, Object... attributes) {
        return buildCard(type, map(attributes));
    }

    public static CardImplBuilder copyOf(Card card) {
        return builder()
                .withType(card.getType())
                .withId(card.getIdOrNull())
                .withAttributes(card.getAllValuesAsMap());
    }

    @Override
    public String toString() {
        return "CardImpl{" + "id=" + id + (isBlank(getCode()) ? "" : format(", code=%s", getCode())) + ", type=" + type.getName() + '}';
    }

    public static class CardImplBuilder implements Builder<CardImpl, CardImplBuilder> {

        private Classe type;
        private final Map<String, Object> rawAttributes = map();
        private Long id;
//		private Long id, currentId, tenantId;
//		private String code, description, user;
//		private ZonedDateTime beginDate, endDate;
//		private CardStatus status;//TODO get status

        public CardImplBuilder withType(Classe type) {
            this.type = type;
            return this;
        }

        public CardImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CardImplBuilder withAttributes(Map<String, Object> attributes) {
            this.rawAttributes.clear();
            this.rawAttributes.putAll(attributes);
            return this;
        }

        public CardImplBuilder addAttributes(Map<String, Object> attributes) {
            this.rawAttributes.putAll(attributes);
            return this;
        }

        public CardImplBuilder addAttribute(String key, @Nullable Object value) {
            this.rawAttributes.put(key, value);
            return this;
        }

        @Override
        public CardImpl build() {
            if (id != null) {
                rawAttributes.put(ATTR_ID, id);
            }
            return new CardImpl(this);
        }

    }

}
