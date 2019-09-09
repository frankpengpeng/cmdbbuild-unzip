/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import javax.activation.DataHandler;

public interface Ws3RequestHandler {

    DataHandler handleRequest(Ws3Request request);

}
