/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.IOException;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.SorterElement.SorterElementDirection;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.logic.mapping.json.Constants.DIRECTION_KEY;
import static org.cmdbuild.logic.mapping.json.Constants.PROPERTY_KEY;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.json.JSONArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmdbSorterUtils {

    private final static ObjectMapper MAPPER = CmdbFilterUtils.MAPPER;

    private static final CmdbSorter NOOP_SORTER = new CmdbSorterImpl(emptyList());

    public static CmdbSorter noopSorter() {
        return NOOP_SORTER;
    }

    public static CmdbSorter parseSorter(@Nullable String json) {
        if (isBlank(json)) {
            return NOOP_SORTER;
        } else {
            return fromJson(json);
        }
    }

    public static CmdbSorter fromJson(JSONArray jsonArray) {
        try {
            String json = MAPPER.writeValueAsString(jsonArray);
            return fromJson(json);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static CmdbSorter fromJson(String json) {
        try {
            JsonNode jsonNode = MAPPER.readTree(json);
            checkArgument(jsonNode.isArray());
            List<SorterElement> list = list();
            for (int i = 0; i < jsonNode.size(); i++) {
                JsonNode element = jsonNode.get(i);
                String property = element.get(PROPERTY_KEY).asText();
                String direction = element.get(DIRECTION_KEY).asText();
                list.add(new SorterElementImpl(property, parseEnum(direction, SorterElementDirection.class)));
            }
            return new CmdbSorterImpl(list);
        } catch (Exception ex) {
            throw runtime(ex, "error deserializing json sorter = '%s'", abbreviate(json));
        }
    }

    public static String toJsonString(CmdbSorter sorter) {
        try {
            return MAPPER.writeValueAsString(sorter.getElements().stream().map((e) -> map(PROPERTY_KEY, e.getProperty(), DIRECTION_KEY, e.getDirection().name())).collect(toList()));
        } catch (JsonProcessingException ex) {
            throw runtime(ex);
        }
    }

}
