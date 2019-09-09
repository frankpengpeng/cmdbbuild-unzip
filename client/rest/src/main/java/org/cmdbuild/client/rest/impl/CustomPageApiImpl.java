/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.api.CustomPageApi;
import org.cmdbuild.client.rest.model.CustomPageInfo;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CustomPageApiImpl extends AbstractServiceClientImpl implements CustomPageApi {

    public CustomPageApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public CustomPageApiResponse upload(InputStream data) {
        checkNotNull(data, "data param cannot be null");
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("custom page upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                .addTextBody("data", toJson(map("description", "")), ContentType.APPLICATION_JSON)
                .build();
        JsonNode jsonNode = post("custompages?merge=true", multipart).asJackson().get("data");
        CustomPageInfo customPageInfo = fromJson(jsonNode, CustomPageInfoImpl.class);
        return new CustomPageApiResponse() {
            @Override
            public CustomPageInfo getCustomPageInfo() {
                return customPageInfo;
            }

            @Override
            public CustomPageApi then() {
                return CustomPageApiImpl.this;
            }
        };
    }

    @JsonDeserialize(builder = CustomPageInfoImplBuilder.class)
    public static class CustomPageInfoImpl implements CustomPageInfo {

        private final long id;
        private final String name, description;

        private CustomPageInfoImpl(CustomPageInfoImplBuilder builder) {
            this.id = (builder.id);
            this.name = checkNotBlank(builder.name);
            this.description = nullToEmpty(builder.description);
        }

        @Override
        public long getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        public static CustomPageInfoImplBuilder builder() {
            return new CustomPageInfoImplBuilder();
        }
    }

    public static class CustomPageInfoImplBuilder implements Builder<CustomPageInfoImpl, CustomPageInfoImplBuilder> {

        private Long id;
        private String name;
        private String description;

        @JsonProperty("_id")
        public CustomPageInfoImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CustomPageInfoImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CustomPageInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        @Override
        public CustomPageInfoImpl build() {
            return new CustomPageInfoImpl(this);
        }

    }

}
