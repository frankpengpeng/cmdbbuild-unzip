/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.config.PreferencesConfiguration;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;
import org.cmdbuild.config.UiConfiguration;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIMEZONE;
import static org.cmdbuild.utils.date.CmDateUtils.systemTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATE_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DECIMAL_SEPARATOR;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_PREFERRED_OFFICE_SUITE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_THOUSANDS_SEPARATOR;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIME_FORMAT_EXTJS;
import static org.cmdbuild.utils.date.ExtjsDateUtils.extjsDateTimeFormatToJavaDateTimeFormat;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@Component
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UiConfiguration uiConfiguration;
    private final UserConfigService userConfigService;
    private final PreferencesConfiguration preferencesConfiguration;

    public UserPreferencesServiceImpl(UiConfiguration uiConfiguration, UserConfigService userConfigService, PreferencesConfiguration preferencesConfiguration) {
        this.uiConfiguration = checkNotNull(uiConfiguration);
        this.userConfigService = checkNotNull(userConfigService);
        this.preferencesConfiguration = checkNotNull(preferencesConfiguration);
    }

    @Override
    public DateAndFormatPreferences getUserPreferences() {
        Map<String, String> config = userConfigService.getForCurrentUsername();
        TimeZone timeZone = systemTimeZone();
        String tzValue = config.get(USER_CONFIG_TIMEZONE);
        if (isNotBlank(tzValue)) {
            try {
                timeZone = TimeZone.getTimeZone(tzValue);
            } catch (Exception ex) {
                logger.error(marker(), "invalid user time zone value =< %s >", tzValue, ex);
            }
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME,//TODO get some system default
                dateFormatter = DateTimeFormatter.ISO_DATE,//TODO get some system default
                timeFormatter = DateTimeFormatter.ISO_TIME;//TODO get some system default
        String dateTimeFormatPattern = "ISO_DATE_TIME",
                dateFormatPattern = "ISO_DATE",
                timeFormatPattern = "ISO_TIME";
        String userDateFormat = config.get(USER_CONFIG_DATE_FORMAT_EXTJS),
                userTimeFormat = config.get(USER_CONFIG_TIME_FORMAT_EXTJS);
        if (isNotBlank(userDateFormat)) {
            try {
                userDateFormat = extjsDateTimeFormatToJavaDateTimeFormat(userDateFormat);
                dateFormatter = DateTimeFormatter.ofPattern(userDateFormat);
                dateFormatPattern = userDateFormat;
            } catch (Exception ex) {
                logger.error(marker(), "invalid user date format value =< %s >", userDateFormat, ex);
            }
        }
        if (isNotBlank(userTimeFormat)) {
            try {
                userTimeFormat = extjsDateTimeFormatToJavaDateTimeFormat(userTimeFormat);
                timeFormatter = DateTimeFormatter.ofPattern(userTimeFormat);
                timeFormatPattern = userTimeFormat;
            } catch (Exception ex) {
                logger.error(marker(), "invalid user time format value =< %s >", userTimeFormat, ex);
            }
        }
        if (isNotBlank(userDateFormat) || isNotBlank(userTimeFormat)) {
            userDateFormat = firstNotBlank(userDateFormat, extjsDateTimeFormatToJavaDateTimeFormat(uiConfiguration.getDateFormat()));//default?
            userTimeFormat = firstNotBlank(userTimeFormat, extjsDateTimeFormatToJavaDateTimeFormat(uiConfiguration.getTimeFormat()));//default?
            String userDateTimeFormat = format("%s %s", userDateFormat, userTimeFormat);//TODO single datetime format config override
            try {
                dateTimeFormatter = DateTimeFormatter.ofPattern(userDateTimeFormat);
                dateTimeFormatPattern = userDateTimeFormat;
            } catch (Exception ex) {
                logger.error(marker(), "invalid user date time format value =< %s >", userDateTimeFormat, ex);
            }
        }
        String decimalSeparator = uiConfiguration.getDecimalsSeparator(),
                userDecSep = config.get(USER_CONFIG_DECIMAL_SEPARATOR);
        try {
            if (isNotBlank(userDecSep)) {
                userDecSep = userDecSep.trim();
                checkArgument(userDecSep.length() == 1);
                decimalSeparator = userDecSep;
            }
        } catch (Exception ex) {
            logger.error(marker(), "invalid user decimal separator value =< %s >", userDecSep, ex);
        }
        String thousandsSeparator = uiConfiguration.getThousandsSeparator(),
                userThoSep = config.get(USER_CONFIG_THOUSANDS_SEPARATOR);
        try {
            if (isNotBlank(userThoSep)) {
                userThoSep = userThoSep.trim();
                checkArgument(userThoSep.length() == 1);
                thousandsSeparator = userThoSep;
            }
        } catch (Exception ex) {
            logger.error(marker(), "invalid user thousands separator value =< %s >", userThoSep, ex);
        }

        PreferredOfficeSuite preferredOfficeSuite = preferencesConfiguration.getPreferredOfficeSuite();
        try {
            preferredOfficeSuite = parseEnumOrDefault(config.get(USER_CONFIG_PREFERRED_OFFICE_SUITE), preferencesConfiguration.getPreferredOfficeSuite());
        } catch (Exception ex) {
            logger.error(marker(), "invalid preferred user office suite value =< %s >", config.get(USER_CONFIG_PREFERRED_OFFICE_SUITE), ex);
        }

        return DateAndFormatPreferencesImpl.builder()
                .withTimezone(timeZone)
                .withDateFormat(dateFormatter)
                .withTimeFormat(timeFormatter)
                .withDateTimeFormat(dateTimeFormatter)
                .withDateTimeFormatPattern(dateTimeFormatPattern)
                .withDateFormatPattern(dateFormatPattern)
                .withTimeFormatPattern(timeFormatPattern)
                .withDecimalSeparator(decimalSeparator)
                .withThousandsSeparator(thousandsSeparator)
                .withPreferredOfficeSuite(preferredOfficeSuite)
                .build();
    }

    @Override
    public UserPrefHelper getUserPreferencesHelper() {
        return new UserPrefHelperImpl(getUserPreferences());
    }

}
