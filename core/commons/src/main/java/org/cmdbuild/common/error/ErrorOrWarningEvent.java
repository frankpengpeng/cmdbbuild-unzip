/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import java.util.Comparator;
import javax.annotation.Nullable;
import org.apache.commons.lang3.exception.ExceptionUtils;

public interface ErrorOrWarningEvent {

    String getMessage();

    @Nullable
    Exception getException();

    @Nullable
    default String getStackTraceAsString() {
        return hasException() ? ExceptionUtils.getStackTrace(getException()) : null;
    }

    ErrorEventLevel getLevel();

    default boolean hasException() {
        return getException() != null;
    }

    enum ErrorEventLevel {
        INFO(2), WARNING(1), ERROR(0);

        private ErrorEventLevel(int level) {
            this.level = level;
        }

        private final int level;

    }

    enum LeveOrderErrorsFirst implements Comparator<ErrorEventLevel> {

        INSTANCE;

        @Override
        public int compare(ErrorEventLevel o1, ErrorEventLevel o2) {
            return Integer.compare(o1.level, o2.level);
        }

    }
}
