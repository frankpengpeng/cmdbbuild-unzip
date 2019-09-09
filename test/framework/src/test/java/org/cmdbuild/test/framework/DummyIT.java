/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static java.util.Collections.emptyMap;
import org.cmdbuild.test.framework.Context;
import org.cmdbuild.test.framework.CmTestRunner;
import org.cmdbuild.test.framework.TestContext;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(CmTestRunner.class)
@Context("dummy")
public class DummyIT {

	public static TestContext dummy() {
		return new TestContext(emptyMap());//TODO add params, test
	}

	@Test
	public void testDummy() {
	}

}
