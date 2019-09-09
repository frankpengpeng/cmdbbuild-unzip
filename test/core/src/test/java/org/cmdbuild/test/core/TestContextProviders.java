/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.core;

import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.test.framework.ContextProvider;
import org.cmdbuild.test.framework.TestContext;
import static org.cmdbuild.test.framework.TestContextUtils.buildTestContext;

@ContextProvider
public class TestContextProviders {

//	@ContextProvider("emptyDb")
    public static TestContext getEmptyDb() {
        return buildTestContext(DatabaseCreator.EMPTY_DUMP);
    }

//	public static TestContext r2u() {
//		return buildTestContext(DatabaseCreator.READY_2_USE_DATABASE);
//	}
}
