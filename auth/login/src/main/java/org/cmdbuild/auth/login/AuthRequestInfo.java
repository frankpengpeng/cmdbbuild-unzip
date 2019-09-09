/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import javax.annotation.Nullable;

/**
 * note: interface used in custom login script: do not change this interface!!
*/
public interface AuthRequestInfo {

    String getRequestUrl();

    String getRequestPath();

    @Nullable
    String getHeader(String name);

    @Nullable
    String getParameter(String name);

    @Nullable
    byte[] getMultipartParameter(String key);

    @Nullable
    default String getMultipartParameterAsString(String key) {
        byte[] data = getMultipartParameter(key);
        return data == null ? null : new String(data);//TODO charset
    }

}
