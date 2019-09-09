/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick;

import static com.google.common.base.Objects.equal;

public interface QuickEventListener {

    String getMethodName();

    String getListenerType();

    default boolean isOfType(String type) {
        return equal(getListenerType(), type);
    }

}
