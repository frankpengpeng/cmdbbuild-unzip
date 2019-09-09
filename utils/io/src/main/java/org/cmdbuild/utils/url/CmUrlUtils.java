/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.url;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import javax.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

public class CmUrlUtils {

    public static String encodeUrlParams(Map<String, ? extends Object> params) {
        try {
            return params.entrySet().stream().map(rethrowFunction(e -> format("%s=%s", URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8.name()), URLEncoder.encode(toStringOrEmpty(e.getValue()), StandardCharsets.UTF_8.name())))).collect(joining("&"));
        } catch (UnsupportedEncodingException ex) {
            throw runtime(ex);
        }
    }

    public static Map<String, String> decodeUrlParams(@Nullable String params) {
        if (isBlank(params)) {
            return emptyMap();
        } else {
            return Splitter.on("&").omitEmptyStrings().trimResults().splitToList(params).stream().map((v) -> {
                try {
                    Matcher matcher = Pattern.compile("([^=]+)=(.*)").matcher(v);
                    if (matcher.matches()) {
                        return Pair.of(matcher.group(1), URLDecoder.decode(matcher.group(2), StandardCharsets.UTF_8.name()));
                    } else {
                        return Pair.of(v, (String) null);
                    }
                } catch (Exception ex) {
                    throw runtime(ex, "error deconding url param token = %s", v);
                }
            }).collect(toImmutableMap(Pair::getKey, Pair::getValue));
        }
    }

    public static byte[] dataUrlToByteArray(String string) {//TODO move this to utils
        Matcher matcher = Pattern.compile("^data:.*;base64,").matcher(string);
        checkArgument(matcher.find());
        string = matcher.replaceFirst("");
        byte[] data = Base64.decodeBase64(string);
        return data;
    }

}
