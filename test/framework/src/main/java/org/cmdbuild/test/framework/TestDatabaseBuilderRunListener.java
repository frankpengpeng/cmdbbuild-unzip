/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class TestDatabaseBuilderRunListener extends RunListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void testRunStarted(Description description) throws Exception {
		logger.info("testRunStarted");
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		logger.info("testRunFinished");
		TestContextHelper.cleanup();
	}

}
