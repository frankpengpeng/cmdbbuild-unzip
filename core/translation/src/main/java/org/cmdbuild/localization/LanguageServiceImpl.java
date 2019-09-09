/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.localization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.common.localization.LanguageService;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.session.inner.SessionDataService;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.translation.RequestLanguageHolder;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 */
@Component
public class LanguageServiceImpl implements LanguageService {

    private static final String LANGUAGE_KEY = "language";

    private final List<LanguageInfo> languages;
    private final List<String> allLanguages;

    private final RequestLanguageHolder requestLanguage;
    private final SessionDataService sessionDataService;
    private final UserConfigService userConfigService;
    private final CoreConfiguration configuration;

    public LanguageServiceImpl(UserConfigService userConfigService, SessionDataService sessionDataService, CoreConfiguration configuration, RequestLanguageHolder requestLanguage) {
        this.sessionDataService = checkNotNull(sessionDataService);
        this.userConfigService = checkNotNull(userConfigService);
        this.configuration = checkNotNull(configuration);
        this.requestLanguage = checkNotNull(requestLanguage);
        languages = readLanguages();
        allLanguages = languages.stream().map(LanguageInfo::getCode).collect(toImmutableList());
    }

    @Override
    public String getDefaultLanguage() {
        return configuration.getDefaultLanguage();
    }

    @Override
    public String getRequestLanguage() {
        return list(
                requestLanguage.getRequestLanguageOrNull(),
                sessionDataService.getCurrentSessionDataSafe().<String>get(LANGUAGE_KEY),
                userConfigService.getForCurrentUsernameOrNull(LANGUAGE_KEY),
                configuration.getDefaultLanguage(),
                Locale.getDefault().toString(),
                "en"
        ).stream().filter(StringUtils::isNotBlank).findFirst().get();
    }

    @Override
    public Collection<String> getEnabledLanguages() {
        return firstNonEmpty(configuration.getEnabledLanguages(), allLanguages);
    }

    @Override
    public Collection<String> getLoginLanguages() {
        return firstNonEmpty(configuration.getLoginLanguages(), allLanguages);
    }

    private static <E> Collection<E> firstNonEmpty(Collection<E> a, Collection<E> b) {
        return a.isEmpty() ? b : a;
    }

    @Override
    public List<LanguageInfo> getAllLanguages() {
        return languages;
    }

    private List<LanguageInfo> readLanguages() {
        List<LanguageInfo> list = fromJson(getClass().getResourceAsStream("/org/cmdbuild/translation/languages.json"), new TypeReference<List<LanguageInfoImpl>>() {
        });
        checkArgument(!isNullOrEmpty(list) && !list.stream().anyMatch(isNull()));
        list = list.stream().sorted(Ordering.natural().onResultOf(LanguageInfo::getCode)).collect(toImmutableList());
        return list;
    }

    private static class LanguageInfoImpl implements LanguageInfo {

        private final String code, description;

        @JsonCreator
        public LanguageInfoImpl(@JsonProperty("code") String code, @JsonProperty("description") String description) {
            this.code = checkNotBlank(code);
            this.description = checkNotBlank(description);
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }
}
