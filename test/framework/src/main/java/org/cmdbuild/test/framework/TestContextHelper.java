/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.framework;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.function.Supplier;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class TestContextHelper {

    private static final InnerTestContextHelper INSTANCE = new InnerTestContextHelper();

    public static TestContext getTestContext(String key, Supplier<TestContext> initializator) {
        return INSTANCE.doGetTestContext(key, initializator);
    }

    public static void cleanup() {
        INSTANCE.doCleanup();
    }

    public static TestContext getCurrentTestContext() {
        return checkNotNull(INSTANCE.threadLocal.get());
    }

    private static class InnerTestContextHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, TestContext> testContextMap = map();
        private final ThreadLocal<TestContext> threadLocal = new ThreadLocal<>();

        private synchronized TestContext doGetTestContext(String key, Supplier<TestContext> initializator) {
            if (!testContextMap.containsKey(key)) {
                testContextMap.put(key, checkNotNull(initializator.get()));
            }
            TestContext testContext = checkNotNull(testContextMap.get(key));
            threadLocal.set(testContext);
            return new TestContext(testContext.getContextMap(), (x) -> {
                threadLocal.remove();
            }, x -> {
                testContext.prepare();
            });
        }

        private synchronized void doCleanup() {
            logger.info("shared test context cleanup");
            list(testContextMap.keySet()).forEach((k) -> {
                logger.info("cleanup of test context = {}", k);
                testContextMap.remove(k).cleanup();
            });
        }
    }

}
