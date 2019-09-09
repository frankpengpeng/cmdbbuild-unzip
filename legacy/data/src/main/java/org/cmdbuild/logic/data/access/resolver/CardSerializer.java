package org.cmdbuild.logic.data.access.resolver;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.cmdbuild.common.Constants;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.beans.DatabaseRecord;

public class CardSerializer<T extends DatabaseRecord> extends AbstractSerializer<T> {

	@Override
	public void visit(DateAttributeType attributeType) {
		java.time.LocalDate date = rawToSystem(attributeType, rawValue);
		java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern(Constants.DATE_PRINTING_PATTERN);

		setAttribute(attributeName, fmt.format(date));
	}

	@Override
	public void visit(TimeAttributeType attributeType) {
		LocalTime date = rawToSystem(attributeType, rawValue);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(Constants.TIME_PRINTING_PATTERN);

		setAttribute(attributeName, fmt.format(date));
	}

	@Override
	public void visit(DateTimeAttributeType attributeType) {
		ZonedDateTime date = rawToSystem(attributeType, rawValue);
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(Constants.DATETIME_PRINTING_PATTERN);

		setAttribute(attributeName, fmt.format(date));
	}

}
