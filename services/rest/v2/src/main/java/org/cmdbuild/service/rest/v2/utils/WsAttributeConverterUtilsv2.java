package org.cmdbuild.service.rest.v2.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.utils.date.CmDateUtils;

import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public class WsAttributeConverterUtilsv2 {

    public static Object toClient(CardAttributeType<?> attributeType, Object value) {
        switch (attributeType.getName()) {
            case DATE:
                return CmDateUtils.toIsoDate(value);
            case TIME:
                return CmDateUtils.toIsoTime(value);
            case TIMESTAMP:
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
                ZonedDateTime date = CmDateUtils.toDateTime(value);
                return date != null ? date.format(formatter) : null;
            case REFERENCE:
            case FOREIGNKEY:
            case LOOKUP:
                return Optional.ofNullable((IdAndDescription) rawToSystem(attributeType, value)).map(IdAndDescription::getId).orElse(null);
            default:
                return value;
        }
    }

}
