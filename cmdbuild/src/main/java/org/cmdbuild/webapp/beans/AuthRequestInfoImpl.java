/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.beans;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import org.cmdbuild.auth.login.AuthRequestInfo;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class AuthRequestInfoImpl implements AuthRequestInfo {

    private final HttpServletRequest request;
    private MultipartHttpServletRequest multipartRequest;

    public AuthRequestInfoImpl(HttpServletRequest request) {
        this.request = Preconditions.checkNotNull(request);
    }

    @Override
    public String getRequestUrl() {
        return request.getRequestURL().toString();
    }

    @Override
    @Nullable
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    @Nullable
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public String getRequestPath() {
        return request.getRequestURI().replaceFirst(Pattern.quote(request.getContextPath()), "");
    }

    @Override
    @Nullable
    public byte[] getMultipartParameter(String key) {
        if (multipartRequest == null) {
            CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartRequest = commonsMultipartResolver.resolveMultipart(request);
            }
        }
        if (multipartRequest != null) {
            MultipartFile file = multipartRequest.getFile(key);
            try {
                return file == null ? null : file.getBytes();
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            return null;
        }
    }

}
