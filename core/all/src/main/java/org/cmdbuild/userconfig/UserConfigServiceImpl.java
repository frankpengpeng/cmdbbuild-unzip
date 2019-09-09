/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Component
public class UserConfigServiceImpl implements UserConfigService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserConfigRepository configRepository;
    private final OperationUserSupplier operationUser;

    public UserConfigServiceImpl(UserConfigRepository configRepository, OperationUserSupplier operationUser) {
        this.configRepository = checkNotNull(configRepository);
        this.operationUser = checkNotNull(operationUser);
    }

    @Override
    public Map<String, String> getByUsername(String username) {
        return configRepository.getByUsername(username);
    }

    @Override
    public void setByUsername(String username, Map<String, String> data) {
        configRepository.setByUsername(username, data);
//        sessionRepository.getSessionsByUsername(username).stream().map(s -> SessionImpl.copyOf(s).addSessionData((Map) data).build()).forEach(sessionRepository::updateSession);//TODO check this
    }

    @Override
    public @Nullable
    Optional<String> getByUsername(String username, String key) {
        logger.debug("get config by usename = {} key = {}", username, key);
        Map<String, String> config = getByUsername(username);
        return config == null ? null : Optional.fromNullable(config.get(key));
    }

    @Override
    public void setByUsername(String username, String key, @Nullable String value) {
        logger.info("set config by usename = {} key = {} value = {}", username, key, value);
        Map<String, String> config = getByUsername(username);
        setByUsername(username, map(config).with(key, value));
    }

    @Override
    public void deleteByUsername(String username, String key) {
        logger.info("delete config by usename = {} key = {}", username, key);
        Map<String, String> config = getByUsername(username);
        setByUsername(username, map(config).withoutKey(key));
    }

    @Override
    @Nullable
    public String getForCurrentUsernameOrNull(String key) {
        return getByUsernameOrNull(operationUser.getUsername(), key);
    }

    @Override
    public Map<String, String> getForCurrentUsername() {
        return getByUsername(operationUser.getUsername());
    }

    @Override
    public void setForCurrent(String key, String value) {
        setByUsername(operationUser.getUsername(), key, value);
    }

}
