/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;
import static org.cmdbuild.userconfig.UserPreferencesUtils.buildDecimalFormat;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite.POS_DEFAULT;

public class DateAndFormatPreferencesImpl implements DateAndFormatPreferences {

    private final DateTimeFormatter dateTimeFormat, dateFormat, timeFormat;
    private final TimeZone timezone;
    private final String decimalSeparator, thousandsSeparator, dateTimeFormatPattern, dateFormatPattern, timeFormatPattern;
    private final DecimalFormat decimalFormat;
    private final PreferredOfficeSuite preferredOfficeSuite;

    private DateAndFormatPreferencesImpl(DateAndFormatPreferencesImplBuilder builder) {
        this.dateTimeFormatPattern = checkNotBlank(builder.dateTimeFormatPattern);
        this.dateFormatPattern = checkNotBlank(builder.dateFormatPattern);
        this.timeFormatPattern = checkNotBlank(builder.timeFormatPattern);
        this.dateTimeFormat = Optional.ofNullable(builder.dateTimeFormat).orElseGet(() -> DateTimeFormatter.ofPattern(dateTimeFormatPattern));
        this.dateFormat = Optional.ofNullable(builder.dateFormat).orElseGet(() -> DateTimeFormatter.ofPattern(dateFormatPattern));
        this.timeFormat = Optional.ofNullable(builder.timeFormat).orElseGet(() -> DateTimeFormatter.ofPattern(timeFormatPattern));
        this.timezone = checkNotNull(builder.timezone);
        this.decimalSeparator = checkNotBlank(builder.decimalSeparator);
        this.thousandsSeparator = emptyToNull(builder.thousandsSeparator);
        this.decimalFormat = buildDecimalFormat(decimalSeparator, thousandsSeparator);
        this.preferredOfficeSuite = firstNotNull(builder.preferredOfficeSuite, POS_DEFAULT);
    }

    @Override
    public DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }

    @Override
    public DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    @Override
    public DateTimeFormatter getTimeFormat() {
        return timeFormat;
    }

    @Override
    public String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    @Override
    public String getDateFormatPattern() {
        return dateFormatPattern;
    }

    @Override
    public String getTimeFormatPattern() {
        return timeFormatPattern;
    }

    @Override
    public TimeZone getTimezone() {
        return timezone;
    }

    @Override
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    @Override
    @Nullable
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    @Override
    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    @Override
    public PreferredOfficeSuite getPreferredOfficeSuite() {
        return preferredOfficeSuite;
    }

    public static DateAndFormatPreferencesImplBuilder builder() {
        return new DateAndFormatPreferencesImplBuilder();
    }

    public static DateAndFormatPreferencesImplBuilder copyOf(DateAndFormatPreferences source) {
        return new DateAndFormatPreferencesImplBuilder()
                .withDateTimeFormatPattern(source.getDateTimeFormatPattern())
                .withDateFormatPattern(source.getDateFormatPattern())
                .withTimeFormatPattern(source.getTimeFormatPattern())
                .withDateTimeFormat(source.getDateTimeFormat())
                .withDateFormat(source.getDateFormat())
                .withTimeFormat(source.getTimeFormat())
                .withTimezone(source.getTimezone())
                .withDecimalSeparator(source.getDecimalSeparator())
                .withThousandsSeparator(source.getThousandsSeparator())
                .withPreferredOfficeSuite(source.getPreferredOfficeSuite());
    }

    public static class DateAndFormatPreferencesImplBuilder implements Builder<DateAndFormatPreferencesImpl, DateAndFormatPreferencesImplBuilder> {

        private DateTimeFormatter dateTimeFormat;
        private DateTimeFormatter dateFormat;
        private DateTimeFormatter timeFormat;
        private TimeZone timezone;
        private String decimalSeparator, dateTimeFormatPattern, dateFormatPattern, timeFormatPattern;
        private String thousandsSeparator;
        private PreferredOfficeSuite preferredOfficeSuite;

        public DateAndFormatPreferencesImplBuilder withPreferredOfficeSuite(PreferredOfficeSuite preferredOfficeSuite) {
            this.preferredOfficeSuite = preferredOfficeSuite;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateTimeFormatPattern(String dateTimeFormatPattern) {
            this.dateTimeFormatPattern = dateTimeFormatPattern;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateFormatPattern(String dateFormatPattern) {
            this.dateFormatPattern = dateFormatPattern;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimeFormatPattern(String timeFormatPattern) {
            this.timeFormatPattern = timeFormatPattern;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateTimeFormat(DateTimeFormatter dateTimeFormat) {
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateFormat(DateTimeFormatter dateFormat) {
            this.dateFormat = dateFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimeFormat(DateTimeFormatter timeFormat) {
            this.timeFormat = timeFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimezone(TimeZone timezone) {
            this.timezone = timezone;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimezone(String timezone) {
            return this.withTimezone(TimeZone.getTimeZone(timezone));
        }

        public DateAndFormatPreferencesImplBuilder withDecimalSeparator(String decimalSeparator) {
            this.decimalSeparator = decimalSeparator;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withThousandsSeparator(String thousandsSeparator) {
            this.thousandsSeparator = thousandsSeparator;
            return this;
        }

        @Override
        public DateAndFormatPreferencesImpl build() {
            return new DateAndFormatPreferencesImpl(this);
        }

    }
}
