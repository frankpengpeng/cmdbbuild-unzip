package org.cmdbuild.test.web.utils;

import javax.annotation.Nullable;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class Login {

    private final String username, password, language, tenant, role;

    public Login(String userName, String password) {
        this(userName, password, null, null, null);
    }

    public Login(String userName, String password, @Nullable String language, @Nullable String tenant, @Nullable String role) {
        this.username = checkNotBlank(userName);
        this.password = checkNotBlank(password);
        this.language = language;
        this.tenant = tenant;
        this.role = role;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Nullable
    public String getLanguage() {
        return language;
    }

    @Nullable
    public String getTenant() {
        return tenant;
    }

    @Nullable
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Login{" + "userName=" + username + '}';
    }

    public Login withUserName(String user) {
        return new Login(user, password, language, tenant, role);
    }

    public Login withPassword(String password) {
        return new Login(username, password, language, tenant, role);
    }

    public Login withLanguage(String language) {
        return new Login(username, password, language, tenant, role);
    }

    public Login withTenant(String tenant) {
        return new Login(username, password, language, tenant, role);
    }

    public Login withRole(String role) {
        return new Login(username, password, language, tenant, role);
    }

    public static Login admin() {
        return new Login("admin", "admin");
    }

    public static Login demo() {
        return new Login("demouser", "demouser");
    }

    public static Login guest() {
        return new Login("guest", "guest");
    }

}
