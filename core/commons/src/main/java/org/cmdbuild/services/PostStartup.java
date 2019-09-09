/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.services;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * bean methods annotated with {@link PostStartup} will be executed after system
 * startup (after {@link SystemStatus.STARTING_SERVICES} and before
 * {@link SystemStatus.READY}).
 *
 * @author davide
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostStartup {

}
