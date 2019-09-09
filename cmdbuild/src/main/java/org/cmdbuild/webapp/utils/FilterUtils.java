/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.utils;

import javax.servlet.http.Cookie;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_AUTHORIZATION_COOKIE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class FilterUtils {

    public static Cookie buildCmdbuildSessionCookie(String contextPath, String sessionToken) {
        checkNotBlank(sessionToken);
        Cookie cookie = new Cookie(CMDBUILD_AUTHORIZATION_COOKIE, sessionToken);
        cookie.setPath(checkNotBlank(contextPath, "cookie context path is null"));
//        cookie.setSecure(true); TODO enable this after 3.1 release
//        cookie.setHttpOnly(true); TODO enable this after 3.1 release
        //TODO set expiration ??
        return cookie;
    }
}
