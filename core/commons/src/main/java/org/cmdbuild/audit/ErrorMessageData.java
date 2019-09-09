/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import org.cmdbuild.common.error.ErrorOrWarningEvent.ErrorEventLevel;
import org.cmdbuild.utils.lang.JsonBean;

@JsonBean(ErrorMessageDataImpl.class)
public interface ErrorMessageData {

    ErrorEventLevel getLevel();

    String getMessage();

    String getStackTrace();

    default boolean isError() {
        switch (getLevel()) {
            case ERROR:
                return true;
            default:
                return false;
        }
    }
}
