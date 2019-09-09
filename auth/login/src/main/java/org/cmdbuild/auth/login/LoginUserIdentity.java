package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

public class LoginUserIdentity {

    private final String value;
    private final LoginType type;

    private LoginUserIdentity(LoginCredentialsBuilder builder) {
        this.value = checkNotNull(builder.value);
        this.type = ofNullable(builder.type).orElseGet(() -> (value.contains("@")) ? LoginType.EMAIL : LoginType.USERNAME);
    }

    public String getValue() {
        return value;
    }

    public LoginType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Login{" + "value=<" + value + ">, type=" + serializeEnum(type) + '}';
    }

    public enum LoginType {
        USERNAME, EMAIL;
    }

    public static LoginCredentialsBuilder builder() {
        return new LoginCredentialsBuilder();
    }

    public static LoginUserIdentity build(String value) {
        return builder().withValue(value).build();
    }

    public static class LoginCredentialsBuilder implements Builder<LoginUserIdentity, LoginCredentialsBuilder> {

        private String value;
        private LoginType type;

        @Override
        public LoginUserIdentity build() {
            return new LoginUserIdentity(this);
        }

        public LoginCredentialsBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public LoginCredentialsBuilder withType(LoginType value) {
            this.type = value;
            return this;
        }

    }

}
