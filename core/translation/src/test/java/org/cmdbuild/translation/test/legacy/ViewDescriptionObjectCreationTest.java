package org.cmdbuild.translation.test.legacy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.cmdbuild.translation.legacy.TranslationObject;
import org.cmdbuild.translation.legacy.Converter;
import org.cmdbuild.translation.legacy.ViewConverter;
import org.cmdbuild.translation.legacy.ViewDescription;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class ViewDescriptionObjectCreationTest {

	private static final String viewName = "viewName";
	private static final String field = "Description";
	private static final String lang = "it";
	private static final String translatedValueDescription = "nomeVista";
	private static final Map<String, String> map = ImmutableMap.of(lang, translatedValueDescription);

	@Test
	public void forDescriptionFieldReturnsValidObject() {
		// given
		final Converter converter = ViewConverter //
				.of(field)//
				.withIdentifier(viewName).withTranslations(map);

		// when
		final TranslationObject translationObject = converter.create();

		// then
		assertTrue(converter.isValid());
		assertNotNull(translationObject);
		assertTrue(ViewDescription.class.cast(translationObject).getName().equals(viewName));
		assertTrue(translationObject.getTranslations().get(lang).equals(translatedValueDescription));
	}

}
