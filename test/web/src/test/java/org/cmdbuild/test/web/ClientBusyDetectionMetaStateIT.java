package org.cmdbuild.test.web;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;

import org.cmdbuild.test.web.utils.*;
import org.junit.After;
import org.junit.Test;
import org.openqa.selenium.JavascriptExecutor;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

//META TEST (test on test facilities)

public class ClientBusyDetectionMetaStateIT extends BaseWebIT {


    @After
    public void cleanup() {
        cleanupDB();
    }

    @Test
    public void feasibilityTest() {

        UITestContext context = getDefaultTestContextInstance();
        //context.withRule(getDefaultCLientLogCheckRule()) ; //(UITestRule.defineClientLogCheckRule("Tolerate a lot of noise (401, 404, blob, constructor) Rule", ImmutableList.of("404","401","blob" , "constructor" , "Cannot read property"), null, null));

        testStart(context);

        goFullScreen();
        login(Login.admin());
        for (int i = 0; i < 20; i++) {
            try {
               Object res=  ((JavascriptExecutor)getDriver()).executeScript("return CMDBuildUI.util.Ajax.currentPending();");
                logger.info("currentPendingCount: {}" , res.toString());
                Thread.currentThread().sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        ExtjsUtils.switchToAdminModule2(getDriver() , context);
        for (int i = 0; i < 60; i++) {
            try {
                LocalDateTime start = LocalDateTime.now();
                Object res=  ((JavascriptExecutor)getDriver()).executeScript("return CMDBuildUI.util.Ajax.currentPending();");
                logger.info("currentPendingCount: {}  Client script took {} ms" , res.toString() ,
                        ChronoUnit.MILLIS.between(start, LocalDateTime.now()));

                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void usageTest() {

        UITestContext context = getDefaultTestContextInstance();
        testStart(context);

        goFullScreen();
        login(Login.admin());
        Boolean afterLogin = ExtjsClientBusyDetectionUtils.DetectionStrategy.defaultStrategy().waitUntilClientIsReady(getDriver(), context);
        logClientBusyDetection(afterLogin , context);
        ExtjsUtils.switchToAdminModule(getDriver() , context);
        Boolean afterSwitchToAdminUI= ExtjsClientBusyDetectionUtils.DetectionStrategy.longWaitStrategy().waitUntilClientIsReady(getDriver(), context);
        logClientBusyDetection(afterSwitchToAdminUI , context);
    }



    private void logClientBusyDetection(Boolean result, UITestContext context) {

        String triggerType = null;
        if (Boolean.TRUE.equals(result))
            triggerType = " ajax requests pending";
        else if (result == null)
            triggerType = "client script execution time trheshold hit";
        else
            triggerType = " no client busy evidence";
        logger.info("After login client busy detection found {} . Accumulated artificial wait is {}", triggerType ,context.getArtificialTestTimeSum());
    }

    /*
        Execution footprint (i7 4700hq , Chromedriver, java in debug mode)

        15:43:56.620 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 73 ms
        15:43:56.726 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 1  Client script took 81 ms
        15:43:57.339 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 587 ms
        15:43:57.704 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 4  Client script took 339 ms
        15:43:57.738 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 9 ms
        15:43:57.769 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 5 ms
        15:43:57.801 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 7 ms
        15:43:57.836 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 9 ms
        15:43:57.866 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 5 ms
        15:43:57.897 [main] INFO  o.c.test.web.DevClientBusyStateIT - currentPendingCount: 0  Client script took 6 ms

     */

}
