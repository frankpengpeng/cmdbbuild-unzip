/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.common.error.ErrorOrWarningEvent;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.xml.CmXmlUtils.isXml;

@CardMapping("_Request")
public class RequestDataImpl implements RequestData {

    private final Long id;
    private final String client, userAgent, path, method, payload, response, responseContentType, payloadContentType, nodeId;
    private final Integer statusCode, payloadSize, responseSize;
    private final ZonedDateTime timestamp;
    private final ErrorMessagesData events;
    private final String sessionId, requestId, actionId, user, query, soapActionOrMethod, logs;
    private final Integer elapsedTimeMillis;
    private final boolean isSoap;

    private RequestDataImpl(RequestDataImplBuilder builder) {
        this.nodeId = builder.nodeId;
        this.sessionId = builder.sessionId;
        this.id = builder.id;
        this.requestId = builder.requestId;
        this.actionId = builder.actionId;
        this.user = builder.user;
        this.query = builder.query;
        this.soapActionOrMethod = builder.soapActionOrMethod;
        this.isSoap = builder.isSoap;
        this.elapsedTimeMillis = (builder.elapsedTimeMillis);
        this.client = checkNotNull(builder.client);
        this.userAgent = checkNotNull(builder.userAgent);
        this.path = checkNotNull(builder.path);
        this.method = checkNotNull(builder.method);
        this.payload = (builder.payload);
        this.payloadContentType = (builder.payloadContentType);
        this.payloadSize = checkNotNull(builder.payloadSize);
        this.response = (builder.response);
        this.responseContentType = (builder.responseContentType);
        this.responseSize = (builder.responseSize);
        this.statusCode = (builder.statusCode);
        this.logs = (builder.logs);
        this.timestamp = checkNotNull(builder.timestamp);
        this.events = firstNonNull(builder.events, new ErrorMessagesData(emptyList()));
    }

    @Override
    @CardAttr
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(value = "ElapsedTime", defaultValue = "0")
    public Integer getElapsedTimeMillis() {
        return elapsedTimeMillis;
    }

    @Override
    @CardAttr
    public String getSessionId() {
        return sessionId;
    }

    @Override
    @CardAttr("NodeId")
    public String getNodeId() {
        return nodeId;
    }

    @Override
    @CardAttr("SessionUser")
    public String getUser() {
        return user;
    }

    @Override
    @CardAttr
    public String getRequestId() {
        return requestId;
    }

    @Override
    @CardAttr
    public String getActionId() {
        return actionId;
    }

    @Override
    @CardAttr
    public String getPath() {
        return path;
    }

    @Override
    @CardAttr
    public String getMethod() {
        return method;
    }

    @Override
    @CardAttr
    public String getQuery() {
        return query;
    }

    @Override
    public String getSoapActionOrMethod() {
        return soapActionOrMethod;
    }

    @Override
    public boolean isSoap() {
        return isSoap;
    }

    @Override
    @CardAttr
    public String getClient() {
        return client;
    }

    @Override
    @CardAttr
    public String getUserAgent() {
        return userAgent;
    }

    @Override
    @CardAttr
    @Nullable
    public String getPayload() {
        return payload;
    }

    @Override
    @Nullable
    @CardAttr
    public String getResponse() {
        return response;
    }

    @Override
    @CardAttr
    @Nullable
    public Integer getPayloadSize() {
        return payloadSize;
    }

    @Override
    @Nullable
    @CardAttr
    public String getPayloadContentType() {
        return payloadContentType;
    }

    @Override
    @Nullable
    @CardAttr
    public String getResponseContentType() {
        return responseContentType;
    }

    @Override
    @CardAttr
    @Nullable
    public Integer getResponseSize() {
        return responseSize;
    }

    @Override
    @CardAttr("StatusCode")
    public Integer getStatusCode() {
        return statusCode;
    }

    @Override
    @CardAttr
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    @CardAttr(readFromDb = false)//TODO add impl to handle this param
    public boolean isCompleted() {
        return getPayload() != null && getResponse() != null && getStatusCode() != null && getElapsedTimeMillis() != null && getResponseSize() != null;
    }

    @CardAttr("Errors")
    @Override
    public ErrorMessagesData getErrorMessageData() {
        return events;
    }

    @Override
    @CardAttr
    @Nullable
    public String getLogs() {
        return logs;
    }

    @Override
    public String toString() {
        String str = "RequestDataImpl{" + "requestId=" + getRequestId() + ", path=" + method + " " + path;
        if (getElapsedTimeMillis() != null) {
            str += format(", elapsed=%.3fs", getElapsedTimeMillis() / 1000d);
        }
        return str + "}";
    }

    public static RequestDataImplBuilder builder() {
        return new RequestDataImplBuilder();
    }

    public static RequestDataImplBuilder copyOf(RequestData data) {
        return new RequestDataImplBuilder().
                withActionId(data.getActionId()).
                withClient(data.getClient()).
                withUserAgent(data.getUserAgent()).
                withElapsedTimeMillis(data.getElapsedTimeMillis()).
                withPath(data.getPath()).
                withMethod(data.getMethod()).
                withPayload(data.getPayload()).
                withPayloadContentType(data.getPayloadContentType()).
                withQuery(data.getQuery()).
                withRequestId(data.getRequestId()).
                withResponse(data.getResponse()).
                withResponseContentType(data.getResponseContentType()).
                withSessionId(data.getSessionId()).
                withUser(data.getUser()).
                withStatusCode(data.getStatusCode()).
                withPayloadSize(data.getPayloadSize()).
                withResponseSize(data.getResponseSize()).
                withTimestamp(data.getTimestamp()).
                withNodeId(data.getNodeId()).
                withErrorMessageData(data.getErrorMessageData())
                .withId(data.getId())
                .withLogs(data.getLogs())
                .withSoap(data.isSoap(), data.getSoapActionOrMethod());
    }

    public static class RequestDataImplBuilder implements Builder<RequestDataImpl, RequestDataImplBuilder> {

        private Long id;
        private String client, userAgent, payload, response, payloadContentType, responseContentType, nodeId, logs;
        private Integer statusCode, payloadSize, responseSize;
        private ZonedDateTime timestamp;
        private ErrorMessagesData events;
        private String sessionId, requestId, actionId, user, path, method, query, soapActionOrMethod;
        private Integer elapsedTimeMillis;
        private boolean isSoap = false;

        private RequestDataImplBuilder() {
        }

        public RequestDataImplBuilder withNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public RequestDataImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        public RequestDataImplBuilder withId(@Nullable Long id) {
            this.id = id;
            return this;
        }

        public RequestDataImplBuilder withSessionId(String sessionId) {
            this.sessionId = checkNotNull(sessionId);
            return this;
        }

        public RequestDataImplBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public RequestDataImplBuilder withRequestId(String requestId) {
            this.requestId = checkNotNull(requestId);
            return this;
        }

        public RequestDataImplBuilder withActionId(String actionId) {
            this.actionId = checkNotNull(actionId);
            return this;
        }

        public RequestDataImplBuilder withPath(String path) {
            this.path = checkNotNull(path);
            return this;
        }

        public RequestDataImplBuilder withMethod(String method) {
            this.method = checkNotNull(method);
            return this;
        }

        public RequestDataImplBuilder withQuery(String query) {
            this.query = query;
            return this;
        }

        public RequestDataImplBuilder withSoap(@Nullable String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withSoapActionOrMethod(String soapActionOrMethod) {
            this.isSoap = true;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withSoap(boolean isSoap, @Nullable String soapActionOrMethod) {
            this.isSoap = isSoap;
            this.soapActionOrMethod = soapActionOrMethod;
            return this;
        }

        public RequestDataImplBuilder withElapsedTimeMillis(Integer elapsedTimeMillis) {
            this.elapsedTimeMillis = (elapsedTimeMillis);
            return this;
        }

        public RequestDataImplBuilder withClient(String client) {
            this.client = checkNotNull(client);
            return this;
        }

        public RequestDataImplBuilder withUserAgent(String userAgent) {
            this.userAgent = checkNotNull(userAgent);
            return this;
        }

        public RequestDataImplBuilder withPayload(String payload) {
            this.payload = (payload);
            return this;
        }

        public RequestDataImplBuilder withPayloadContentType(String payloadContentType) {
            this.payloadContentType = (payloadContentType);
            return this;
        }

        public RequestDataImplBuilder withResponse(String response) {
            this.response = (response);
            return this;
        }

        public RequestDataImplBuilder withResponseContentType(String responseContentType) {
            this.responseContentType = (responseContentType);
            return this;
        }

        public RequestDataImplBuilder withStatusCode(Integer statusCode) {
            this.statusCode = (statusCode);
            return this;
        }

        public RequestDataImplBuilder withPayloadSize(Integer payloadSize) {
            this.payloadSize = checkNotNull(payloadSize);
            return this;
        }

        public RequestDataImplBuilder withResponseSize(Integer responseSize) {
            this.responseSize = (responseSize);
            return this;
        }

        public RequestDataImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = checkNotNull(timestamp);
            return this;
        }

        public RequestDataImplBuilder withErrorsAndWarningEvents(@Nullable List<ErrorOrWarningEvent> events) {
            if (events != null) {
                this.events = ErrorMessagesData.fromErrorsAndWarningEvents(events);
            }
            return this;
        }

        public RequestDataImplBuilder withErrorMessageData(@Nullable ErrorMessagesData events) {
            this.events = events;
            return this;
        }

        public RequestDataImplBuilder withErrorMessageDataList(List<ErrorMessageData> events) {
            this.events = new ErrorMessagesData((List) events);
            return this;
        }

        @Override
        public RequestDataImpl build() {
            isSoap = isSoapRequest(path, payload);
            if (isSoap) {
                soapActionOrMethod = getSoapActionOrMethod(payload);
            }
            return new RequestDataImpl(this);
        }

        //TODO only for orm tool, refactor and remove
        public void setCompleted(String param) {
            //do nothig
        }

        private boolean isSoapRequest(String path, @Nullable String payload) {
            return path.contains("soap") && isXml(payload);
        }

        private @Nullable
        String getSoapActionOrMethod(String payload) {
            Matcher matcher = Pattern.compile("[<][^:]+[:]Body[>][<][^:]+[:]([a-zA-Z0-9]+)").matcher(payload);
            if (matcher.find()) {
                return trimToNull(matcher.group(1));
            } else {
                return null;
            }
        }
    }
}
