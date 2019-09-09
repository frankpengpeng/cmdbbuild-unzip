/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.date;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import static java.time.ZoneOffset.UTC;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import java.time.temporal.Temporal;
import static java.util.Arrays.asList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.joda.time.ReadableInstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmDateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String dateTimeFileSuffix() {
        return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
    }

    public static TimeZone systemTimeZone() {
        return TimeZone.getDefault();
    }

    public static ZoneId systemZoneOffset() {
        return ZoneId.systemDefault();
    }

    public static ZonedDateTime now() {
        return systemDate();
    }

    public static ZonedDateTime systemDate() {
        return ZonedDateTime.now(ZoneOffset.UTC);
    }

    public static @Nullable
    java.sql.Date toSqlDate(@Nullable Object dateTime) { //TODO check conversion and return value
        if (dateTime == null) {
            return null;
        } else if (dateTime instanceof java.sql.Date) {
            return (java.sql.Date) dateTime;
        } else {
            Date javaDate = toJavaDate(dateTime);
            if (javaDate == null) {
                return null;
            } else {
                return new java.sql.Date(javaDate.getTime());
            }
        }
    }

    public static @Nullable
    Timestamp toSqlTimestamp(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else if (dateTime instanceof Timestamp) {
            return (Timestamp) dateTime;
        } else if (dateTime instanceof ZonedDateTime) {
            return new Timestamp(((ZonedDateTime) dateTime).toInstant().toEpochMilli());
        } else {
            return toSqlTimestamp(toDateTime(dateTime));
        }
    }

    public static @Nullable
    Time toSqlTime(@Nullable Object localTime) {
        if (localTime == null) {
            return null;
        } else if (localTime instanceof Time) {
            return (Time) localTime;
        } else if (localTime instanceof LocalTime) {
            return new Time(toJavaDate(localTime).getTime());//TODO test/check this
        } else {
            return toSqlTime(toTime(localTime));
        }
    }

    public static @Nullable
    String toIsoDateTime(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(toDateTime(dateTime));
        }
    }

    public static @Nullable
    String toIsoDateTimeUtc(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            ZonedDateTime zonedDateTime = toDateTime(dateTime);
            zonedDateTime = zonedDateTime.withZoneSameInstant(UTC);
            return DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(zonedDateTime);
        }
    }

    private final static DateTimeFormatter READABLE_DATE_TIME = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public static @Nullable
    String toUserReadableDateTime(@Nullable Object dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return READABLE_DATE_TIME.format(toDateTime(dateTime).withZoneSameInstant(systemZoneOffset()));
        }
    }

    public static String getReadableTimezoneOffset() {
        return systemZoneOffset().getId();
    }

    public static @Nullable
    String toIsoDate(@Nullable Object date) {
        LocalDate localDate = toDate(date);
        if (localDate == null) {
            return null;
        } else {
            return localDate.toString();
        }
    }

    private final static DateTimeFormatter ISO_LOCAL_TIME_NO_MILLIS = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .toFormatter();

    public static @Nullable
    String toIsoTime(@Nullable Object value) {
        return toIsoTime(toTime(value));
    }

    public static @Nullable
    String toIsoTimeWithNanos(@Nullable Object value) {
        return CmDateUtils.toIsoTimeWithNanos(toTime(value));
    }

    public static @Nullable
    String toIsoTime(@Nullable LocalTime localTime) {
        if (localTime == null) {
            return null;
        } else {
            return ISO_LOCAL_TIME_NO_MILLIS.format(localTime);
        }
    }

    public static @Nullable
    String toIsoTimeWithNanos(@Nullable LocalTime localTime) {
        if (localTime == null) {
            return null;
        } else {
            return DateTimeFormatter.ISO_LOCAL_TIME.format(localTime);
        }
    }

    public static @Nullable
    LocalTime toTime(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof LocalTime) {
            return (LocalTime) value;
        } else if (value instanceof String) {
            if (isBlank((String) value)) {
                return null;
            } else {
                return parseLocalTime((String) value);
            }
        } else if (value instanceof Number) {
            return toDateTime(value).toLocalTime();
        } else if (value instanceof Date) {
            return toTime(((Date) value).getTime());
        } else if (value instanceof ReadableInstant) {
            return toTime(((ReadableInstant) value).getMillis());
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to TIME type", value, value.getClass()));
        }
    }
    private final static List<DateTimeFormatter> TIME_FORMATTERS = ImmutableList.copyOf(asList(
            DateTimeFormatter.ISO_LOCAL_TIME.withResolverStyle(ResolverStyle.LENIENT),
            DateTimeFormatter.ISO_DATE_TIME));

    private static LocalTime parseLocalTime(String value) {
        for (DateTimeFormatter dateTimeFormatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with time format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        throw new IllegalArgumentException(format("unsupported time format, unable to parse string = '%s'", value));
    }

    @Nullable
    public static LocalDate toDate(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof java.sql.Date) {
            return ((java.sql.Date) value).toLocalDate();
        } else if (value instanceof String) {
            if (isBlank((String) value)) {
                return null;
            } else {
                return parseLocalDate((String) value);
            }
        } else if (value instanceof Number) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Number) value).longValue()), ZoneOffset.UTC).toLocalDate();
        } else if (value instanceof Date) {
            return toDate(((Date) value).getTime());
        } else if (value instanceof ReadableInstant) {
            return toDate(((ReadableInstant) value).getMillis());
        } else if (value instanceof Temporal) {
            return LocalDate.from((Temporal) value);
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to DATE type", value, value.getClass()));
        }
    }

    @Nullable
    public static LocalDate toDateAtTimeZone(@Nullable Object value, TimeZone timezone) {
        checkNotNull(timezone, "timezone is null");
        ZonedDateTime dateTime = toDateTime(value);
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.withZoneSameInstant(timezone.toZoneId()).toLocalDate();
        }
    }

    private final static List<java.time.format.DateTimeFormatter> DATE_FORMATTERS = ImmutableList.copyOf(asList(
            java.time.format.DateTimeFormatter.ISO_DATE.withResolverStyle(ResolverStyle.LENIENT),
            java.time.format.DateTimeFormatter.ISO_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT),
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yy")));

    private static java.time.LocalDate parseLocalDate(String value) {
        for (java.time.format.DateTimeFormatter dateTimeFormatter : DATE_FORMATTERS) {
            try {
                return java.time.LocalDate.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with date format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        throw new IllegalArgumentException(format("unsupported date format, unable to parse string = '%s'", value));
    }

    public static @Nullable
    ZonedDateTime toDateTime(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof ZonedDateTime) {
            return (ZonedDateTime) value;
        } else if (value instanceof String) {
            if (isBlank((String) value)) {
                return null;
            } else {
                return parseDateTime((String) value);
            }
        } else if (value instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) value, ZoneOffset.UTC);
        } else if (value instanceof Number) {
            return toDateTime(Instant.ofEpochMilli(((Number) value).longValue()));
        } else if (value instanceof Date) {
            return toDateTime(((Date) value).getTime());
        } else if (value instanceof ReadableInstant) {
            return toDateTime(((ReadableInstant) value).getMillis());
        } else if (value instanceof Calendar) {
            return toDateTime(((Calendar) value).toInstant());
        } else {
            throw new IllegalArgumentException(format("unable to convert value %s of type %s to java date type", value, value.getClass()));
        }
    }

    private final static List<DateTimeFormatter> DATE_TIME_PARSING_FORMATTERS = ImmutableList.of(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT),
            new DateTimeFormatterBuilder().parseCaseInsensitive().append(ISO_LOCAL_DATE_TIME).appendOffset("+HHmm", "Z").toFormatter().withResolverStyle(ResolverStyle.LENIENT),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withResolverStyle(ResolverStyle.LENIENT).withZone(systemZoneOffset()));//TODO check user timezone offset (?)

    private static ZonedDateTime parseDateTime(String value) {
        for (DateTimeFormatter dateTimeFormatter : DATE_TIME_PARSING_FORMATTERS) {
            try {
                return ZonedDateTime.parse(value, dateTimeFormatter);
            } catch (IllegalArgumentException | DateTimeParseException e) {
                LOGGER.debug("unable to parse string value = '{}' with date format = {}", value, dateTimeFormatter);
                LOGGER.trace("date parsing error", e);
            }
        }
        try {
            LocalDate localDate = parseLocalDate(value);
            return ZonedDateTime.of(localDate.atStartOfDay(), ZoneOffset.UTC);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            LOGGER.debug("unable to parse string value = '{}' as local date", value);
            LOGGER.trace("date parsing error", e);
        }
        throw new IllegalArgumentException(format("unsupported date/time format, unable to parse string = '%s'", value));
    }

    public static @Nullable
    Date toJavaDate(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        } else if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof ReadableInstant) {
            return toJavaDate(((ReadableInstant) value).getMillis());
        } else if (value instanceof ZonedDateTime) {
            return toJavaDate(((ZonedDateTime) value).toInstant().toEpochMilli());
        } else if (value instanceof LocalDate) {
            return toJavaDate(((LocalDate) value).atTime(12, 0).toInstant(ZoneOffset.UTC).toEpochMilli());
        } else if (value instanceof LocalTime) {
            return toJavaDate(((LocalTime) value).atDate(LocalDate.of(1970, 1, 1)).toInstant(ZoneOffset.UTC).toEpochMilli());
        } else {
            ZonedDateTime dateTime;
            try {
                dateTime = toDateTime(value);
            } catch (Exception ex) {
                throw new IllegalArgumentException(format("unable to convert value %s of type %s to java date type", value, value.getClass()), ex);
            }
            if (dateTime == null) {
                return null;
            } else {
                return toJavaDate(dateTime);
            }
        }
    }

    public static String toIsoDuration(long millis) {
        return toIsoDuration(Duration.ofMillis(millis));
    }

    public static String toUserDuration(long millis) {
        return toUserDuration(Duration.ofMillis(millis));
    }

    @Nullable
    public static String toUserDuration(@Nullable Duration duration) {
        return duration == null ? null : duration.toString()
                .substring(2)
                .replaceAll("(\\d[HMS])(?!$)", "$1 ")
                .toLowerCase();
    }

    @Nullable
    public static String toUserDuration(@Nullable String duration) {
        return toUserDuration(toDuration(duration));
    }

    public static String toIsoDuration(Duration duration) {
        return duration.toString();
    }

    @Nullable
    public static Duration toDuration(@Nullable String duration) {
        return isBlank(duration) ? null : Duration.parse(duration);
    }

    public static boolean isDate(@Nullable Object value) {
        return value != null && LocalDate.class.isAssignableFrom(value.getClass());

    }

    public static boolean isTime(@Nullable Object value) {
        return value != null && LocalTime.class.isAssignableFrom(value.getClass());

    }

    public static boolean isDateTime(@Nullable Object value) {
        return value != null && isDateTime(value.getClass());
    }

    public static boolean isDateTime(Class classe) {
        return ZonedDateTime.class.isAssignableFrom(classe) || Instant.class.isAssignableFrom(classe) || Date.class.isAssignableFrom(classe) || ReadableInstant.class.isAssignableFrom(classe) || Calendar.class.isAssignableFrom(classe);
    }

}
