/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.utils;

import static com.google.common.base.Strings.nullToEmpty;
import javax.annotation.Nullable;

public class WsRequestUtils {

    public static boolean isAdminViewMode(@Nullable String viewModeParam) {
        return nullToEmpty(viewModeParam).equalsIgnoreCase("admin");
    }

}
