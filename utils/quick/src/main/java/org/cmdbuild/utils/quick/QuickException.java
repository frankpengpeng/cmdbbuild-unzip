/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.quick;

import static java.lang.String.format;

public class QuickException extends RuntimeException {

    public QuickException() {
    }

    public QuickException(String message, Object... args) {
        super(format(message, args));
    }

    public QuickException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

    public QuickException(Throwable cause) {
        super(cause);
    }

}
