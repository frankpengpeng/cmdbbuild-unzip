/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.error;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmExceptionUtils.exceptionToMessage;

public class ErrorOrWarningEventImpl implements ErrorOrWarningEvent {

    private final String message;
    private final ErrorEventLevel level;
    private final Exception exception;

    public ErrorOrWarningEventImpl(@Nullable String message, ErrorEventLevel level, @Nullable Exception exception) {
        checkArgument(isNotBlank(message) || exception != null, "must provide message or exception");
        if (message == null) {
            this.message = exceptionToMessage(exception);
        } else {
            String exMessage = exceptionToMessage(exception);
            if (isBlank(exMessage)) {
                this.message = message;
            } else {
                this.message = format("%s: %s", message, exMessage);
            }
        }
        this.level = checkNotNull(level);
        this.exception = exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public ErrorEventLevel getLevel() {
        return level;
    }

    @Override
    @Nullable
    public Exception getException() {
        return exception;
    }

}
