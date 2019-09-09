/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.shark.test.utils;

import com.lutris.logging.LogAdapter;
import com.lutris.logging.LogChannel;
import com.lutris.logging.LogWriter;
import com.lutris.util.Config;
import com.lutris.util.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class SharkDatabaseManagerLoggerForTest extends com.lutris.logging.Logger {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public SharkDatabaseManagerLoggerForTest(boolean something) {
	}

	@Override
	public LogChannel getChannel(String string) {
		return new LogAdapter() {
			@Override
			public boolean isEnabled(int i) {
				return true;
			}

			@Override
			public boolean isEnabled(String string) {
				return true;
			}

			@Override
			public int getLevel(String string) {
				return DEBUG;
			}

			@Override
			public LogWriter getLogWriter(String string) {
				return new LogWriter(this, string) {
				};
			}

			@Override
			public LogWriter getLogWriter(int i) {
				return new LogWriter(this, i) {
				};
			}

			@Override
			public void write(int i, String string) {
				logger.debug(string);
			}

			@Override
			public void write(String string, String string1) {
				logger.debug(string1);
			}

			@Override
			public void write(int i, String string, Throwable thrwbl) {
				logger.debug(string, thrwbl);
			}

			@Override
			public void write(String string, String string1, Throwable thrwbl) {
				logger.debug(string1, thrwbl);
			}
		};
	}

	@Override
	public void configure(String string) throws ConfigException {

	}

	@Override
	public void configure(Config config) throws ConfigException {

	}

}
