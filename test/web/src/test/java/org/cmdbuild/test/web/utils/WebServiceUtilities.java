package org.cmdbuild.test.web.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WebServiceUtilities {

    public static Logger logger = LoggerFactory.getLogger(WebServiceUtilities.class);

    public static List<JsonObject> toList(JsonArray jsonArray) {

        ArrayList<JsonObject> list = new ArrayList<>(jsonArray.size());
        for (JsonElement jsonElement : jsonArray) {
            list.add(jsonElement.getAsJsonObject());
        }
        return list;
    }

}
