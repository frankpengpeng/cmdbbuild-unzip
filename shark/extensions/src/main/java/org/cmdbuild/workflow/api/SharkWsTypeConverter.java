package org.cmdbuild.workflow.api;

import org.cmdbuild.workflow.WorkflowTypeDefaults;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.Constants.DATETIME_PRINTING_PATTERN;
import static org.cmdbuild.common.Constants.DATE_PRINTING_PATTERN;
import static org.cmdbuild.common.Constants.SOAP_ALL_DATES_PRINTING_PATTERN;
import static org.cmdbuild.common.Constants.TIME_PRINTING_PATTERN;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.cmdbuild.api.fluent.Card;
import org.cmdbuild.api.fluent.ws.WsFluentApiExecutor.WsType;
import org.cmdbuild.workflow.inner.AttributeInfo;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SharkWsTypeConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    /**
     * Usable by subclasses only.
     */
    protected SharkWsTypeConverter() {
    }

    protected abstract WorkflowApi workflowApi();

    protected abstract SharkWsTypeConverterConfig configuration();

    protected String toWsType(final AttributeInfo attributeInfo, final Object value) {
        if (value == null) {
            return EMPTY;
        }
        try {
            return unsafeToWsType(attributeInfo, value);
        } catch (final RuntimeException e) {
            LOGGER.warn("error converting to ws type, continuing anyway passing stringified value: " + e.getMessage());
            return value.toString();
        }
    }

    private String unsafeToWsType(final AttributeInfo attributeInfo, final Object value) {
        switch (((WsType) attributeInfo.getWsType())) {
            case DATE:
            case TIMESTAMP:
            case TIME:
                return new SimpleDateFormat(SOAP_ALL_DATES_PRINTING_PATTERN).format((Date) value);

            case FOREIGNKEY:
            case REFERENCE:
                ReferenceType reference = (ReferenceType) value;
                return reference.checkValidity() ? Long.toString(reference.getId()) : EMPTY;

            case LOOKUP:
                LookupType lookup = (LookupType) value;
                return lookup.checkValidity() ? Long.toString(lookup.getId()) : EMPTY;

            default:
                return value.toString();
        }
    }

    protected Object toClientType(final AttributeInfo attributeInfo, final String wsValue) {
        try {
            return unsafeToClientType(attributeInfo, wsValue);
        } catch (final Exception e) {
            throw new IllegalArgumentException("unexpected value format", e);
        }
    }

    protected Object unsafeToClientType(final AttributeInfo attributeInfo, final String wsValue)
            throws NumberFormatException {
        switch (((WsType) attributeInfo.getWsType())) {
            case BOOLEAN:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultBoolean() : Boolean.parseBoolean(wsValue);

            case DATE:
            case TIMESTAMP:
            case TIME:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultDate() : parseAllDateFormats(wsValue);

            case DECIMAL:
            case DOUBLE:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultDouble() : Double.parseDouble(wsValue);

            case FOREIGNKEY:
            case REFERENCE:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultReference() : referenceType(attributeInfo, wsValue);

            case INTEGER:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultInteger() : Long.parseLong(wsValue);

            case LOOKUP:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultLookup() : lookupType(wsValue);

            case CHAR:
            case STRING:
            case TEXT:
                return isBlank(wsValue) ? WorkflowTypeDefaults.defaultString() : wsValue;

            default:
                return wsValue;
        }
    }

    /**
     * We should be able to parse timestamp/date/time values in all available
     * formats since we don't have a unique one.
     */
    private Date parseAllDateFormats(final String wsValue) {
        final String[] formats = new String[]{SOAP_ALL_DATES_PRINTING_PATTERN, DATETIME_PRINTING_PATTERN,
            DATE_PRINTING_PATTERN, TIME_PRINTING_PATTERN};
        for (final String format : formats) {
            try {
                LOGGER.debug("trying parsing using format '{}'", format);
                return new SimpleDateFormat(format).parse(wsValue);
            } catch (final ParseException e) {
                LOGGER.warn(format("error parsing using format '%s', trying next one", format), e);
            }
        }
        LOGGER.warn("error parsing using all formats");
        return null;
    }

    private ReferenceType referenceType(final AttributeInfo attributeInfo, final String wsValue) {
        final Integer id = Integer.parseInt(wsValue);
        final Optional<String> className = attributeInfo.getTargetClassName();
        final ReferenceType referenceType;
        if (className.isPresent()) {
            final Card card;
            if (configuration().legacyReference()) {
                card = workflowApi().existingCard(className.get(), id);
            } else {
                card = workflowApi().existingCard(className.get(), id).withDescription(EMPTY);
            }
            referenceType = workflowApi().referenceTypeFrom(card);
        } else {
            referenceType = workflowApi().referenceTypeFrom(id);
        }
        return referenceType;
    }

    private LookupType lookupType(final String wsValue) {
        final Integer id = Integer.parseInt(wsValue);
        return workflowApi().selectLookupById(id);
    }

}
