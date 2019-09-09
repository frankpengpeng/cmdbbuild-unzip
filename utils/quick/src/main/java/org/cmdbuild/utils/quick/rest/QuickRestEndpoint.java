/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick.rest;

import javax.servlet.http.HttpServletRequest;

public interface QuickRestEndpoint {

    int getPriority();

    String getName();

    boolean matchesRequest(QRestMethod method, String path);

    String getOutputContentType();

    Object invokeMethod(String path, HttpServletRequest request);


}
