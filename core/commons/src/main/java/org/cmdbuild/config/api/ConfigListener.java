/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.cmdbuild.services.SystemStatus;
import static org.cmdbuild.services.SystemStatus.SYST_READY;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigListener {

    Class value();

    SystemStatus[] requireSystemStatus() default {SYST_READY};
}
