/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.user;

import static com.google.common.base.MoreObjects.firstNonNull;
import java.time.ZonedDateTime;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;

import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.auth.user.UserDataImpl.USER_CLASS_NAME;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;

@CardMapping(USER_CLASS_NAME)
public class UserDataImpl implements UserData {

    private final Long id;
    private final String description, username, password, email;
    private final boolean isActive, isService;
    private final ZonedDateTime passwordExpiration, lastPasswordChange, lastExpiringNotification;

    private UserDataImpl(UserDataImplBuilder builder) {
        this.id = builder.id;
        this.description = builder.description;
        this.username = checkNotBlank(builder.username);
        this.password = builder.password;
        this.email = builder.email;
        this.isActive = firstNonNull(builder.isActive, true);
        this.isService = firstNonNull(builder.isService, false);
        this.passwordExpiration = builder.passwordExpiration;
        this.lastPasswordChange = builder.lastPasswordChange;
        this.lastExpiringNotification = builder.lastExpiringNotification;
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    @Nullable
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getUsername() {
        return username;
    }

    @Override
    @CardAttr
    @Nullable
    public String getPassword() {
        return password;
    }

    @Override
    @CardAttr
    @Nullable
    public String getEmail() {
        return email;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr
    public boolean isService() {
        return isService;
    }

    @Override
    @CardAttr
    @Nullable
    public ZonedDateTime getPasswordExpiration() {
        return passwordExpiration;
    }

    @Override
    @CardAttr
    @Nullable
    public ZonedDateTime getLastPasswordChange() {
        return lastPasswordChange;
    }

    @Override
    @CardAttr
    @Nullable
    public ZonedDateTime getLastExpiringNotification() {
        return lastExpiringNotification;
    }

    public static UserDataImplBuilder builder() {
        return new UserDataImplBuilder();
    }

    public static UserDataImplBuilder copyOf(UserData source) {
        return new UserDataImplBuilder()
                .withId(source.getId())
                .withDescription(source.getDescription())
                .withUsername(source.getUsername())
                .withPassword(source.getPassword())
                .withEmail(source.getEmail())
                .withActive(source.isActive())
                .withService(source.isService())
                .withPasswordExpiration(source.getPasswordExpiration())
                .withLastPasswordChange(source.getLastPasswordChange())
                .withLastExpiringNotification(source.getLastExpiringNotification());
    }

    public static class UserDataImplBuilder implements Builder<UserDataImpl, UserDataImplBuilder> {

        private Long id;
        private String description;
        private String username;
        private String password;
        private String email;
        private Boolean isActive;
        private Boolean isService;
        private ZonedDateTime passwordExpiration;
        private ZonedDateTime lastPasswordChange;
        private ZonedDateTime lastExpiringNotification;

        public UserDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public UserDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public UserDataImplBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserDataImplBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserDataImplBuilder withClearPassword(String password) {
            return this.withPassword(isBlank(password) ? null : CmLegacyPasswordUtils.encrypt(password));
        }

        public UserDataImplBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDataImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserDataImplBuilder withService(Boolean isService) {
            this.isService = isService;
            return this;
        }

        public UserDataImplBuilder withPasswordExpiration(ZonedDateTime passwordExpiration) {
            this.passwordExpiration = passwordExpiration;
            return this;
        }

        public UserDataImplBuilder withLastPasswordChange(ZonedDateTime lastPasswordChange) {
            this.lastPasswordChange = lastPasswordChange;
            return this;
        }

        public UserDataImplBuilder withLastExpiringNotification(ZonedDateTime lastExpiringNotification) {
            this.lastExpiringNotification = lastExpiringNotification;
            return this;
        }

        @Override
        public UserDataImpl build() {
            return new UserDataImpl(this);
        }

    }
}
