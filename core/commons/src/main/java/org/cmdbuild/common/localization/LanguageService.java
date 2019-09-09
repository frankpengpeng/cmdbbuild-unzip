/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.localization;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.common.localization.LanguageUtils.toLocale;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author davide
 */
public interface LanguageService extends LanguageConfiguration {

	String getRequestLanguage();

	default Locale getRequestLocale() {
		return toLocale(getRequestLanguage());
	}

	List<LanguageInfo> getAllLanguages();

	default LanguageInfo getLanguageInfo(String code) {
		checkNotBlank(code);
		return getAllLanguages().stream().filter((l) -> equal(l.getCode(), code)).collect(onlyElement());
	}

	Collection<String> getEnabledLanguages();

	Collection<String> getLoginLanguages();

	default Collection<LanguageInfo> getEnabledLanguagesInfo() {
		Collection<String> enabled = getEnabledLanguages();
		return getAllLanguages().stream().filter((l) -> enabled.contains(l.getCode())).collect(toList());
	}

	default Collection<LanguageInfo> getLoginLanguagesInfo() {
		Collection<String> enabled = getLoginLanguages();
		return getAllLanguages().stream().filter((l) -> enabled.contains(l.getCode())).collect(toList());
	}

	default boolean isLocalizationEnabled() {
		return true;
	}

}
