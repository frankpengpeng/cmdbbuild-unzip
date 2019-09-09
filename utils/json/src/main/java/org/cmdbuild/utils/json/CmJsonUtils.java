/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonElement;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmJsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static TypeReference<Map<String, String>> MAP_OF_STRINGS = new TypeReference<Map<String, String>>() {
    };
    public final static TypeReference<List<String>> LIST_OF_STRINGS = new TypeReference<List<String>>() {
    };

    public final static TypeReference<Map<String, Object>> MAP_OF_OBJECTS = new TypeReference<Map<String, Object>>() {
    };

    private final static ObjectMapper OBJECT_MAPPER = new ObjectMapper() {
        {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }
    };
    private final static ObjectMapper OBJECT_MAPPER_PRETTY = new ObjectMapper() {
        {
            configure(SerializationFeature.INDENT_OUTPUT, true);
        }
    };

    public static <T> T fromJson(String json, Class<T> classe) {
        try {
            return OBJECT_MAPPER.readValue(json, classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(JsonNode record, Class<T> classe) {
        try {
            return OBJECT_MAPPER.readValue(record.traverse(), classe);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(JsonNode record, TypeReference type) {
        try {
            return OBJECT_MAPPER.readValue(record.traverse(), type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(InputStream in, TypeReference type) {
        try {
            return OBJECT_MAPPER.readValue(in, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static <T> T fromJson(String json, TypeReference type) {
        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static @Nullable
    String nullableToJson(@Nullable Object value) {
        return value == null ? null : toJson(value);
    }

    public static boolean isJson(String value) {
        return isNotBlank(value) && (value.startsWith("{") || value.startsWith("["));//TODO improve this
    }

    public static String toJson(Object value) {
        try {
            return OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String toPrettyJson(Object value) {
        try {
            return OBJECT_MAPPER_PRETTY.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Nullable
    public static String toString(@Nullable JsonElement value) {
        if (value == null || value.isJsonNull()) {
            return null;
        } else if (value.isJsonPrimitive()) {
            return value.getAsString();
        } else {
            return value.toString();
        }
    }

    @Nullable
    public static String prettifyIfJson(@Nullable String mayBeJson) {
        if (isNotBlank(mayBeJson) && mayBeJson.matches("^[\\{\\[].*")) {//TODO better json check
            try {
                Object object = fromJson(mayBeJson, Object.class);
                return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
            } catch (Exception ex) {
                LOGGER.warn("unable to prettify (maybe)json = {}", mayBeJson);
                LOGGER.debug("unable to prettify xml", ex);
                return mayBeJson;
            }
        } else {
            return mayBeJson;
        }
    }

    @Nullable
    public static Object prettifyIfJsonLazy(@Nullable String mayBeJson) {
        return new Object() {

            @Override
            public String toString() {
                return String.valueOf(prettifyIfJson(mayBeJson));
            }

        };
    }

}
