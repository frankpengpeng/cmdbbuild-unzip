/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Long.parseLong;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.job.MapperConfig;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EmailUtils {

    private final static Tika TIKA = new Tika();

    private final static String WORKFLOW_MAPPING_PARAM_SEPARATOR = "&#124;";

    public static Map<String, String> parseWorkflowMappingParam(@Nullable String value) {
        if (isBlank(value)) {
            return emptyMap();
        } else {
            return Splitter.on(WORKFLOW_MAPPING_PARAM_SEPARATOR).splitToList(value).stream().map(l -> {
                Matcher matcher = Pattern.compile("\\s*([^=]+?)\\s*=\\s*(.*?)\\s*").matcher(l);
                checkArgument(matcher.matches(), "invalid workflow mapping param syntax for part = %s value = %s", l, value);
                return Pair.of(checkNotBlank(matcher.group(1)), matcher.group(2));
            }).collect(toImmutableMap(Pair::getKey, Pair::getValue));
        }
    }

    public static String serializeEmailStatus(EmailStatus status) {
        return status.name().toLowerCase().replaceFirst("es_", "");
    }

    public static @Nullable
    EmailStatus parseEmailStatus(@Nullable String status) {
        return parseEnumOrNull(status, EmailStatus.class);
    }

    @Nullable
    public static Long parseCardIdFromEmailSubject(@Nullable String subject) {
        Matcher matcher = Pattern.compile("\\[\\s*([0-9]+)\\s*\\]").matcher(nullToEmpty(subject));
        if (matcher.find()) {
            return parseLong(matcher.group(1));
        } else {
            return null;
        }
    }

    public static String parseSubjectDescrFromEmailSubject(@Nullable String subject) {
        Matcher matcher = Pattern.compile("\\[\\s*[0-9]+\\s*\\]\\s*(.*)").matcher(nullToEmpty(subject));
        if (matcher.find()) {
            return nullToEmpty(matcher.group(1));
        } else {
            return nullToEmpty(subject);
        }
    }

    public static String buildSubjectWithIdForEmail(Email email) {
        if (email.getNoSubjectPrefix()) {
            return nullToEmpty(email.getSubject());
        } else {
            return format("[%s] %s", checkNotNull(email.getId()), nullToEmpty(email.getSubject()));
        }
    }

    public static List<String> parseEmailReferencesHeader(@Nullable String value) {
        return Splitter.onPattern("\\s+").omitEmptyStrings().trimResults().splitToList(nullToEmpty(value)).stream().map(EmailUtils::parseEmailHeaderToken).collect(toImmutableList());
    }

    public static String parseEmailHeaderToken(@Nullable String rawToken) {
        return nullToEmpty(rawToken).replaceFirst("\\s*[<](.+)[>]\\s*", "$1");
    }

    public static String formatEmailHeaderToken(String value) {
        return format("<%s>", checkNotBlank(value));
    }

    public static String processMapperExpr(MapperConfig config, String body, String expr) {
        if (isBlank(body) || isBlank(expr)) {
            return "";
        } else {
            Matcher matcher = Pattern.compile(format("%s(.*?)%s\\s*%s(.*?)%s", Pattern.quote(config.getKeyBegin()), Pattern.quote(config.getKeyEnd()), Pattern.quote(config.getValueBegin()), Pattern.quote(config.getValueEnd())), Pattern.DOTALL).matcher(body);
            while (matcher.find()) {
                if (equal(matcher.group(1), expr)) {
                    return matcher.group(2);
                }
            }
            return "";
        }
    }

}
