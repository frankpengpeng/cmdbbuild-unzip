/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 *
 */
public class LoggerConfigImpl implements LoggerConfig {

	private final String category, level;

	public LoggerConfigImpl(String category, String level) {
		this.category = checkNotNull(trimToNull(category));
		this.level = checkNotNull(trimToNull(level)).toUpperCase();
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getLevel() {
		return level;
	}

	@Override
	public String toString() {
		return "SimpleLoggerConfig{" + "category=" + category + ", level=" + level + '}';
	}

}
