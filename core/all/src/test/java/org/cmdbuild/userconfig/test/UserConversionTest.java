package org.cmdbuild.userconfig.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.time.format.DateTimeFormatter;
import org.cmdbuild.userconfig.UserPrefHelper;
import org.cmdbuild.userconfig.DateAndFormatPreferences;
import org.cmdbuild.userconfig.DateAndFormatPreferencesImpl;
import org.cmdbuild.userconfig.UserPrefHelperImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class UserConversionTest {

    @Test
    public void testUserPrefHelper() {
        DateAndFormatPreferences config = DateAndFormatPreferencesImpl.builder()
                .withTimezone("UTC")
                .withDateFormat(DateTimeFormatter.ISO_DATE)
                .withTimeFormat(DateTimeFormatter.ISO_TIME)
                .withDateTimeFormat(DateTimeFormatter.ISO_DATE_TIME)
                .withDateTimeFormatPattern("ISO_DATE_TIME")
                .withDateFormatPattern("ISO_DATE")
                .withTimeFormatPattern("ISO_TIME")
                .withDecimalSeparator(".")
                .withThousandsSeparator("")
                .build();

        UserPrefHelper helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-10T00:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));

        config = DateAndFormatPreferencesImpl.copyOf(config).withTimezone("Europe/Rome").build();
        helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-09T23:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));

        config = DateAndFormatPreferencesImpl.copyOf(config).withTimezone("America/New_York").build();
        helper = new UserPrefHelperImpl(config);

        assertEquals("1988-12-10T05:00:00Z", toIsoDateTimeUtc(helper.parseDateTime("1988-12-10T00:00:00")));
    }
}
