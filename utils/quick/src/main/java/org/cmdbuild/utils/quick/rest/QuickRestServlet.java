/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static org.cmdbuild.utils.quick.rest.QuickRestUtils.getServletId;

public class QuickRestServlet extends HttpServlet {

    private static final Map<String, QuickRestServlet> SERVLET_REGISTRY = new ConcurrentHashMap<>();

    private QuickRestService service;

    @Override
    public void init() throws ServletException {
        String servletId = getServletId(getServletContext(), getServletConfig());
        SERVLET_REGISTRY.put(servletId, this);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        service.handleServletRequest(req, resp);
    }

    protected static void doRegisterQuickRestService(String key, QuickRestService service) {
        QuickRestServlet servlet = checkNotNull(SERVLET_REGISTRY.get(key), "quick servlet not found for id = %s", key);
        servlet.service = checkNotNull(service);
    }

}
