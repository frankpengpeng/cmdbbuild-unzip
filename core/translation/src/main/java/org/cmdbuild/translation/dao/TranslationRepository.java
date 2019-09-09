/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation.dao;

import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;

public interface TranslationRepository {

	List<Translation> getTranslations(String code);

	String getTranslation(String code, String lang);

	PagedElements<Translation> getTranslations(@Nullable String filter, @Nullable Integer offset, @Nullable Integer limit);

	Translation setTranslation(String code, String lang, String value);

	void deleteTranslationIfExists(String code, String lang);

	void deleteTranslations(String code);

	@Nullable
	String getTranslationOrNull(String code, String lang);

	List<Translation> getAllForLanguages(Set<String> languages);
}
