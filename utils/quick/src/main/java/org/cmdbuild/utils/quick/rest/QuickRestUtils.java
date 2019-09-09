/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.rest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

public class QuickRestUtils {

    public static String getServletId(ServletContext servletContext, ServletConfig servletConfig) {
        return key(servletContext.getContextPath(), servletConfig.getServletName());
    }

    public static String buildServletId(ServletContext servletContext, String servletName) {
        return key(servletContext.getContextPath(), servletName);
    }

    public static void registerQuickRestService(ServletContext servletContext, String servletName, QuickRestService service) {
        registerQuickRestService(buildServletId(servletContext, servletName), service);
    }

    public static void registerQuickRestService(String key, QuickRestService service) {
        QuickRestServlet.doRegisterQuickRestService(key, service);
    }

}
