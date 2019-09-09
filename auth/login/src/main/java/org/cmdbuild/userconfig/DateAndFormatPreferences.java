/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import java.text.DecimalFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;

public interface DateAndFormatPreferences {

    DateTimeFormatter getDateTimeFormat();

    DateTimeFormatter getDateFormat();

    DateTimeFormatter getTimeFormat();

    String getDateTimeFormatPattern();

    String getDateFormatPattern();

    String getTimeFormatPattern();

    TimeZone getTimezone();

    String getDecimalSeparator();

    String getThousandsSeparator();

    DecimalFormat getDecimalFormat();
    
    PreferredOfficeSuite getPreferredOfficeSuite();

    default boolean hasThousandsSeparator() {
        return isNotBlank(getThousandsSeparator());
    }

    default ZoneId getZoneId() {
        return getTimezone().toZoneId();
    }

}
