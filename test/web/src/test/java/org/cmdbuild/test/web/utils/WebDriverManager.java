/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.test.web.utils;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;
import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebDriverManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RemoteWebDriver driver;

    private File chromedriverFile;

    public void init() throws Exception {
        String webDriverParam = trimToEmpty(System.getProperty("cmdbuild.test.webdriver"));
        if (webDriverParam.startsWith("http://")) {
            try {
                logger.info("start remote web driver with url =< {} >", webDriverParam);
                driver = new RemoteWebDriver(new URL(webDriverParam), new ChromeOptions());
            } catch (Exception ex) {
                throw runtime(ex, "error starting remote web driver with url =< %s >", webDriverParam);
            }
        } else {
            try {
                logger.info("start local chrome web driver");
                chromedriverFile = new File(tempDir(), "chromedriver");
                FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/chromedriver_linux64"), chromedriverFile);
                chromedriverFile.setExecutable(true);
                System.setProperty("webdriver.chrome.driver", chromedriverFile.getAbsolutePath());
                ChromeOptions options = new ChromeOptions();
                driver = new ChromeDriver(options);
            } catch (Exception ex) {
                throw runtime(ex, "error starting local chrome web driver");
            }
        }
    }

    public void cleanup() {
        logger.info("web driver cleanup");
        if (driver != null) {
            driver.quit();
            driver = null;
        }
        if (chromedriverFile != null) {
            deleteQuietly(chromedriverFile.getParentFile());
            chromedriverFile = null;
        }
    }

    public RemoteWebDriver getDriver() {
        return checkNotNull(driver);
    }

}
