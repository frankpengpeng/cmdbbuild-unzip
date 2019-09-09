/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.core.fluentapi;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.springframework.stereotype.Component;

@Component
public class SystemApiImpl implements SystemApi {

    private final GlobalConfigService configService;
    private final DaoService dao;

    public SystemApiImpl(GlobalConfigService configService, DaoService dao) {
        this.configService = checkNotNull(configService);
        this.dao = checkNotNull(dao);
    }

    @Override
    public void executeQuery(String query) {
        dao.getJdbcTemplate().execute(query);
    }

    @Override
    @Nullable
    public String getSystemConfig(String key) {
        return configService.getStringOrDefault(key);
    }

}
