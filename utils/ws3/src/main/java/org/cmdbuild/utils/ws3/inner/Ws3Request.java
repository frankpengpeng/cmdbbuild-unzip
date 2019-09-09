/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

public interface Ws3Request {

    String getService();

    String getMethod();

    String getParam(String key);

}
