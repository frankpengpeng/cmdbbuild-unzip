package org.cmdbuild.service.rest.common.utils;

import java.util.Optional;
import org.cmdbuild.dao.beans.IdAndDescription;
import org.cmdbuild.utils.date.CmDateUtils;

import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;

public class WsAttributeConverterUtils {

	public static Object toClient(CardAttributeType<?> attributeType, Object value) {
		switch (attributeType.getName()) {
			case DATE:
				return CmDateUtils.toIsoDate(value);
			case TIME:
				return CmDateUtils.toIsoTime(value);
			case TIMESTAMP:
				return CmDateUtils.toIsoDateTime(value);
			case REFERENCE:
			case FOREIGNKEY:
			case LOOKUP:
				return Optional.ofNullable((IdAndDescription) rawToSystem(attributeType, value)).map(IdAndDescription::getId).orElse(null);
			default:
				return value;
		}
	}

}
