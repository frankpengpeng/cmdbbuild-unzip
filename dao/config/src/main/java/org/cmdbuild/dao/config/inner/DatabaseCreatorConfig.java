/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface DatabaseCreatorConfig {

    boolean installPostgres();

    boolean createDatabase();

    boolean useLimitedUser();

    boolean useSharkSchema();

    boolean keepLocalConfig();

    ConfigImportStrategy getConfigImportStrategy();

    String getCmdbuildUser();

    String getCmdbuildPassword();

    String getLimitedUser();

    String getLimitedPassword();

    String getDatabaseUrl();

    String getHost();

    int getPort();

    String getDatabaseName();

    String getAdminUser();

    String getAdminPassword();

    String getDatabaseType();

    String getSqlPath();

    Map<String, String> getConfig();

    void checkConfig();

    @Nullable
    String getTablespace();

    default boolean hasAdminUser() {
        return isNotBlank(getAdminUser());
    }

    default boolean isLimitedUserNotEqualToAdminUser() {
        return !equal(getLimitedUser(), getAdminUser());
    }

}
