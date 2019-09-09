package org.cmdbuild.test.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.cmdbuild.test.web.utils.BaseWebIT;
import org.cmdbuild.test.web.utils.Login;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.Test;

public class LoginWebIT extends BaseWebIT {

    @Before
    public void init() {
        goFullScreen();
        getUrl(getBaseUrlUI());
    }

    @Test
    public void loginTest() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.admin());
        loginFormSelectLanguage("English");
        submitLoginForm();

        waitForOrFail(this::isUserLoggedIn, "login failed");
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void loginTest2() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.admin());
        loginFormSelectFirstlanguage();
        submitLoginForm();

        waitForOrFail(this::isUserLoggedIn, "login failed");
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void loginTest3() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.demo());
        loginFormSelectLanguage("English");
        submitLoginForm();

        waitForOrFail(this::loginFormPageIsShowingSecondLoginStep);
        assertFalse(isUserLoggedIn());

        loginFormSelectFirstRoleIfAny();
        loginFormSelectFirstTenantIfAny();
        submitLoginForm();

        waitForOrFail(this::isUserLoggedIn, "login failed");
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void logoutTest() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.admin());
        loginFormSelectLanguage("English");
        submitLoginForm();

        waitForOrFail(this::isUserLoggedIn, "login failed");

        clickLogoutElement();

        waitForOrFail(this::isUserAtLoginPage, "logout failed");
        //TODO check session
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void wrongPasswordTest() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.admin().withPassword("WrongPassword"));
        loginFormSelectLanguage("English");
        submitLoginForm();

        assertTrue(isUserAtLoginPage());//TODO improve this
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void userNotExistingLoginMustFailTest() {
        loginFormInputUsernameAndPasswordInLoginForm(Login.admin().withUserName("SomeInvalidUsername"));
        loginFormSelectLanguage("English");
        submitLoginForm();

        assertTrue(isUserAtLoginPage());//TODO improve this
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void existingUserCaseMispelledLoginMustFailIfCaseSensitiveTest() {
        setCmdbuildSystemConfig("org.cmdbuild.auth.case.insensitive", "false");
        Login credentials = Login.admin().withUserName(Login.admin().getUserName().toUpperCase());
        assertNotEquals(credentials.getUserName(), Login.admin().getUserName());

        loginFormInputUsernameAndPasswordInLoginForm(credentials);
        loginFormSelectLanguage("English");
        submitLoginForm();

        assertTrue(isUserAtLoginPage());//TODO improve this 
        checkNoErrorsOnBrowserConsole();
    }

    @Test
    public void existingUserCaseMispelledLoginMustSucceedIfCaseInsensitiveTest() {
        setCmdbuildSystemConfig("org.cmdbuild.auth.case.insensitive", "true");
        Login credentials = Login.admin().withUserName(Login.admin().getUserName().toUpperCase());
        assertNotEquals(credentials.getUserName(), Login.admin().getUserName());

        loginFormInputUsernameAndPasswordInLoginForm(credentials);
        loginFormSelectLanguage("English");
        submitLoginForm();

        waitForOrFail(this::isUserLoggedIn, "login failed");
        checkNoErrorsOnBrowserConsole();
    }

}
