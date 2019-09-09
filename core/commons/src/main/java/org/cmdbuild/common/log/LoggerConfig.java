/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

/**
 * configuration info for one logger (ie category + level)
 *
 * @author davide
 */
public interface LoggerConfig {

	String getCategory();

	String getLevel();

}
