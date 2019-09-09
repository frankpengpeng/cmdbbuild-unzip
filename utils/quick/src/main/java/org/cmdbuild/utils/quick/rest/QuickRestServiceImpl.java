/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.rest;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ComparisonChain;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.io.ByteStreams.copy;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import java.util.Collection;
import java.util.List;
import javax.activation.DataHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickRestServiceImpl implements QuickRestService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<QuickRestEndpoint> endpointList;
    private final ExceptionMapper exceptionMapper;

    public QuickRestServiceImpl(Collection<QuickRestEndpoint> endpointList, ExceptionMapper exceptionMapper) {
        this.endpointList = endpointList.stream().sorted((a, b) -> ComparisonChain.start().compare(a.getPriority(), b.getPriority()).compare(a.getName(), b.getName()).result()).collect(toImmutableList());
        this.exceptionMapper = checkNotNull(exceptionMapper);
    }

    @Override
    public void handleServletRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {

            QuickRestEndpoint endpoint;
            String path = req.getRequestURL().toString();//TODO check this
            QRestMethod method = QRestMethod.valueOf(req.getMethod().toUpperCase());
            try {
                endpoint = checkNotNull(endpointList.stream().filter(e -> e.matchesRequest(method, path)).findFirst().orElse(null), "invalid rest ws endpoint = %s %s : no handler was found", method, path);
            } catch (Exception ex) {
                logger.error("error resolving endpoint for request = %s %s", method, path, ex);
                resp.setStatus(500);//TODO check this
                printResponse(resp, "RESOURCE NOT FOUND", "text/plain");//TODO
                return;
            }

            Object response;
            try {
                response = endpoint.invokeMethod(path, req);
            } catch (Exception ex) {
                Response exResponse = exceptionMapper.toResponse(ex);
                resp.setStatus(exResponse.getStatus());//TODO check this
                printResponse(resp, exResponse.getEntity(), toStringOrDefault(exResponse.getMediaType(), "application/octet-stream"));//TODO
                return;
            }

            printResponse(resp, response, endpoint.getOutputContentType());

        } catch (Exception ex) {
            logger.error("unexpected request processing error", ex);
            resp.setStatus(500);//TODO check this
            printResponse(resp, "UNEXPECTED ERROR", "text/plain");//TODO
        }
    }

    private void printResponse(HttpServletResponse resp, Object response, String contentType) throws IOException {
        if (response instanceof DataHandler) {
            DataHandler dataHandler = (DataHandler) response;
            String fileName = dataHandler.getName();
            if (isNotBlank(fileName)) {
                resp.addHeader("Content-Disposition", format("inline; filename=\"%s\"", encodeFileName(fileName)));
            }
            contentType = dataHandler.getContentType();
            if (isNotBlank(contentType)) {
                resp.addHeader("Content-Type", contentType);
            }
            copy(dataHandler.getInputStream(), resp.getOutputStream());
        } else {
            resp.addHeader("Content-Type", contentType);
            if (!(response instanceof String || response instanceof byte[])) {
                if (equal(contentType, "application/json")) { //TODO improve this
                    response = toJson(response);
                } else {
                    throw new IllegalArgumentException(format("invalid response data =< %s > ( %s ) with content type = %s ", response, getClassOfNullable(response).getName(), contentType));
                }
            }
            if (response instanceof String) {
                String str = (String) response;
                resp.getWriter().write(str);
            } else {
                byte[] data = (byte[]) response;
                resp.getOutputStream().write(data);
            }
        }
    }

    private String encodeFileName(String name) {
        try {
            checkNotBlank(name);
            return encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("error encoding name", e);
            return name;
        }
    }

}
