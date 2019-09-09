/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.uniqueIndex;
import java.io.IOException;
import java.io.StringWriter;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.common.error.ErrorAndWarningCollectorService.marker;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeGroupInfo;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.springframework.stereotype.Component;
import org.cmdbuild.translation.dao.TranslationRepository;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.translation.dao.Translation;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

@Component
public class TranslationServiceImpl implements TranslationService, ObjectTranslationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseRepository classeRepository;
    private final TranslationRepository translationRepository;
    private final LanguageService languageService;

    public TranslationServiceImpl(ClasseRepository classeRepository, TranslationRepository translationRepository, LanguageService languageService) {
        this.classeRepository = checkNotNull(classeRepository);
        this.translationRepository = checkNotNull(translationRepository);
        this.languageService = checkNotNull(languageService);
    }

    @Override
    public String translateAttributeDescription(Attribute attribute) {
        String value = translateByCode(format("attributeclass.%s.%s.description", attribute.getOwner().getName(), attribute.getName()));
        String defaultValue = attribute.getDescription();
        if (attribute.getOwner() instanceof Classe) {
            Classe classe = ((Classe) attribute.getOwner());
            String name = attribute.getName();
            while ((attribute == null || attribute.isInherited()) && value == null && classe.hasParent()) {
                classe = classeRepository.getClasse(classe.getParent());
                attribute = classe.getAttributeOrNull(name);
                if (attribute != null) {
                    value = translateByCode(format("attributeclass.%s.%s.description", attribute.getOwner().getName(), attribute.getName()));
                }
            }
        }
        return firstNonNull(value, nullToEmpty(defaultValue));
    }

    @Override
    public String translateAttributeGroupDescription(EntryType owner, AttributeGroupInfo attributeGroup) {
        String value = translateByCode(format("attributegroupclass.%s.%s.description", owner.getName(), attributeGroup.getName()));
        String defaultValue = attributeGroup.getDescription();
        if (owner.isClasse()) {
            Classe classe = ((Classe) owner);
            while (value == null) {
                Classe parent = classe.hasParent() ? classeRepository.getClasse(classe.getParent()) : null;
                if (parent == null || !parent.hasAttributeGroup(attributeGroup.getName()) || !equal(parent.getAttributeGroup(attributeGroup.getName()).getDescription(), attributeGroup.getDescription())) {
                    break;
                } else {
                    value = translateByCode(format("attributegroupclass.%s.%s.description", parent.getName(), attributeGroup.getName()));
                    classe = parent;
                }
            }
        }
        return firstNonNull(value, nullToEmpty(defaultValue));
    }

    @Override
    public String translateExpr(String source) {
        Matcher matcher = Pattern.compile("[{]translate:[^}]*[}]", Pattern.DOTALL).matcher(source);
        if (matcher.find()) {
            StringBuffer stringBuffer = new StringBuffer();
            matcher.reset();
            while (matcher.find()) {
                String from = matcher.group();
                Matcher blockMatcher = Pattern.compile("[{]translate:([^:}]*)(:[^}]*)?[}]").matcher(from);
                checkArgument(blockMatcher.find());
                String code = checkNotBlank(blockMatcher.group(1));
                String defaultValue = emptyToNull(blockMatcher.group(2));
                String value = translateByCode(code);
                if (value == null) {
                    if (defaultValue != null) {
                        value = defaultValue.replaceFirst(":", "");
                    } else {
                        logger.warn(marker(), "translation not found for code =< {} > and user language =< {} >", code, languageService.getRequestLanguage());
                        value = format("missing_translation('%s')", code);
                    }
                }
                matcher.appendReplacement(stringBuffer, Matcher.quoteReplacement(value));
            }
            matcher.appendTail(stringBuffer);
            return stringBuffer.toString();
        } else {
            return source;
        }
    }

    @Nullable
    @Override
    public String translateByCode(String code) {
        checkNotBlank(code);
        String lang = languageService.getRequestLanguage();
        if (isBlank(lang)) {
            return null;
        } else {
            return translationRepository.getTranslationOrNull(code, lang);
        }
    }

    @Override
    public String getTranslationForCodeAndCurrentUser(String code) {
        return checkNotNull(translateByCode(code), format("unable to find translation for code =< {} > and user language =< {} >", code, languageService.getRequestLanguage()));
    }

    @Override
    public String getTranslationValueForCodeAndLang(String code, String lang) {
        return translationRepository.getTranslation(code, lang);
    }

    @Override
    public Map<String, String> getTranslationValueMapByLangForCode(String code) {
        return translationRepository.getTranslations(code).stream().collect(toMap(Translation::getLang, Translation::getValue));
    }

    @Override
    public PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit) {
        return translationRepository.getTranslations(filter, offset, limit);
    }

    @Override
    public Translation setTranslation(String code, String lang, String value) {
        return translationRepository.setTranslation(code, lang, value);
    }

    @Override
    public void deleteTranslationIfExists(String code, String lang) {
        translationRepository.deleteTranslationIfExists(code, lang);
    }

    @Override
    public void deleteTranslations(String code) {
        translationRepository.deleteTranslations(code);
    }

    @Override
    public TranslationExportHelper exportHelper() {
        return new TranslationExportHelperImpl();
    }

    private class TranslationExportHelperImpl implements TranslationExportHelper {

        private Set<String> languages;
        private String separator;

        @Override
        public TranslationExportHelper withLanguages(@Nullable Collection<String> languages) {
            this.languages = CmCollectionUtils.emptyToNull(set(CmCollectionUtils.nullToEmpty(languages)));
            return this;
        }

        @Override
        public TranslationExportHelper withSeparator(String separator) {
            this.separator = separator;
            return this;
        }

        @Override
        public DataHandler export() {
            try {
                if (isNullOrEmpty(languages)) {
                    languages = set(languageService.getEnabledLanguages());
                }
                if (isBlank(separator)) {
                    separator = Character.valueOf((char) CsvPreference.STANDARD_PREFERENCE.getDelimiterChar()).toString();
                }
                CsvPreference csvPreference = new CsvPreference.Builder(CsvPreference.STANDARD_PREFERENCE.getQuoteChar(), separator.charAt(0), CsvPreference.STANDARD_PREFERENCE.getEndOfLineSymbols()).build();

                List<Translation> translations = translationRepository.getAllForLanguages(languages);
                Map<String, Translation> byLangCode = uniqueIndex(translations, t -> key(t.getLang(), t.getCode()));
                StringWriter writer = new StringWriter();
                try (CsvListWriter csv = new CsvListWriter(writer, csvPreference)) {
                    csv.write(list("identifier", "description", "default").with(languages));
                    translations.stream().map(Translation::getCode).sorted().distinct().forEach(rethrowConsumer(code -> {
                        String description = "",//TODO description
                                defaultValue = "";//TODO default
                        csv.write(list(code, description, defaultValue).accept((line) -> languages.stream().map(lang -> Optional.ofNullable(byLangCode.get(key(lang, code))).map(Translation::getValue).orElse("")).forEach(line::add)));
                    }));
                }
                return newDataHandler(writer.toString().getBytes(StandardCharsets.UTF_8), "text/csv", "translations.txt");
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }

    }
}
