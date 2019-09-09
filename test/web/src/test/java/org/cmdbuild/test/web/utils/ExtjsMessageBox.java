package org.cmdbuild.test.web.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.cmdbuild.test.web.utils.ExtjsUtils.*;

public class ExtjsMessageBox {

    private static int defaultWaitSeconds = 2; //keep low, you know when expect msgboxes and they should render themselves quickly
    private static int pollingIntervalMillis = 50;

    private static By msgBoxLocator = By.className("x-message-box");
    private static By msgBoxHeaderLocator = By.className("x-header");
    private static By msgBoxBodyLocator = By.className("x-window-body");
    private static By msgBoxTitleLocator = By.className("x-title-text");
    private static By msgBoxMessageLocator = By.className("x-window-text");

    private static Logger logger = LoggerFactory.getLogger(ExtjsMessageBox.class);


    public static  boolean isAnyMessageBoxShown(WebDriver driver) {
        return isAnyMessageBoxShown(driver , defaultWaitSeconds);
    }

    public static  boolean isAnyMessageBoxShown(WebDriver driver, int maxWaitInSeconds) {
        Optional<WebElement> msgBox  = waitForElementPresence(driver, msgBoxLocator, maxWaitInSeconds, pollingIntervalMillis);
        return msgBox.isPresent();
    }

    //supposes there is only one msgbox active TODO: check if true
    public static String getMsgBoxTitle(WebDriver driver) {
        Optional<WebElement> msgBox  = waitForElementPresence(driver, msgBoxLocator, defaultWaitSeconds, pollingIntervalMillis);
        WebElement header = waitForPresenceOfNestedElement(driver, msgBoxHeaderLocator, msgBox.get());
        WebElement title = waitForPresenceOfNestedElement(driver, msgBoxTitleLocator, header);
        return title.getText();
    }

    //supposes there is only one msgbox active TODO: check if true
    public static String getMsgBoxMessage(WebDriver driver) {
        Optional<WebElement> msgBox  = waitForElementPresence(driver, msgBoxLocator, defaultWaitSeconds, pollingIntervalMillis);
        WebElement body = waitForPresenceOfNestedElement(driver, msgBoxBodyLocator, msgBox.get());
        WebElement text = waitForPresenceOfNestedElement(driver, msgBoxMessageLocator, body);
        return text.getText();
    }


}
