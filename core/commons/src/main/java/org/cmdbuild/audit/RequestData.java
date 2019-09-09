/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.List;
import java.util.Optional;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import org.apache.commons.codec.Charsets;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.audit.RequestDataUtils.getSinglePlaintextPart;
import static org.cmdbuild.audit.RequestDataUtils.hasSinglePlaintextPart;
import static org.cmdbuild.audit.RequestDataUtils.isMultipartWithOnlyPlaintextParts;
import static org.cmdbuild.audit.RequestDataUtils.isPlaintext;
import static org.cmdbuild.audit.RequestInfo.NO_SESSION_ID;
import static org.cmdbuild.audit.RequestDataUtils.payloadStringToBinaryData;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;

/**
 * request data (for request tracking); classes implementing this interface are
 * supposed to be immutable
 */
public interface RequestData extends RequestInfo {

    @Nullable
    Long getId();

    String getClient();

    String getUserAgent();

    ErrorMessagesData getErrorMessageData();

    @Nullable
    String getPayload();

    @Nullable
    String getPayloadContentType();

    @Nullable
    Integer getPayloadSize();

    @Nullable
    String getResponse();

    @Nullable
    String getResponseContentType();

    @Nullable
    Integer getResponseSize();

    @Nullable
    String getLogs();

    default List<ErrorMessageData> getErrorOrWarningEvents() {
        return (List) getErrorMessageData().getData();
    }

    default boolean hasLogs() {
        return isNotBlank(getLogs());
    }

    default boolean hasSession() {
        return !equal(getSessionId(), NO_SESSION_ID) && isNotBlank(getSessionId());
    }

    default boolean hasPayload() {
        return isNotBlank(getPayload());
    }

    default boolean hasResponse() {
        return isNotBlank(getResponse());
    }

    default boolean isBinaryPayload() {
        return hasPayload() && !isPlaintext(getPayloadContentType());
    }

    default byte[] getBinaryPayload() {
        if (isBinaryPayload()) {
            return payloadStringToBinaryData(getPayload());
        } else {
            return nullToEmpty(getPayload()).getBytes(Charsets.UTF_8);
        }
    }

    default boolean isBinaryResponse() {
        return hasResponse() && !isPlaintext(getResponseContentType());
    }

    default byte[] getBinaryResponse() {
        if (isBinaryResponse()) {
            return payloadStringToBinaryData(getResponse());
        } else {
            return nullToEmpty(getResponse()).getBytes(Charsets.UTF_8);
        }
    }

    default String getBestPlaintextResponse() {
        if (isBinaryResponse()) {
            DataSource dataSource = newDataSource(getBinaryResponse(), getResponseContentType());
            if (isMultipartWithOnlyPlaintextParts(dataSource)) {
                if (hasSinglePlaintextPart(dataSource)) {
                    return getSinglePlaintextPart(dataSource);
                } else {
                    return new String(getBinaryResponse(), Charsets.UTF_8);
                }
            }
        }
        return getResponse();
    }

    default String getBestPlaintextPayload() {
        if (isBinaryPayload()) {
            DataSource dataSource = newDataSource(getBinaryPayload(), getPayloadContentType());
            if (isMultipartWithOnlyPlaintextParts(dataSource)) {
                if (hasSinglePlaintextPart(dataSource)) {
                    return getSinglePlaintextPart(dataSource);
                } else {
                    return new String(getBinaryPayload(), Charsets.UTF_8);
                }
            }
        }
        return getPayload();
    }
}
