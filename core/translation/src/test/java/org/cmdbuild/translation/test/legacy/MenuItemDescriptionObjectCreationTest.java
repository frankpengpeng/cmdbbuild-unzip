package org.cmdbuild.translation.test.legacy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.cmdbuild.translation.legacy.TranslationObject;
import org.cmdbuild.translation.legacy.ClassConverter;
import org.cmdbuild.translation.legacy.Converter;
import org.cmdbuild.translation.legacy.ClassDescription;
import org.cmdbuild.translation.legacy.MenuItemDescription;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class MenuItemDescriptionObjectCreationTest {

	private static final String menuItemUuid = "uuid";
	private static final String field = "Description";
	private static final String lang = "it";
	private static final String translatedValueDescription = "Archivio";
	private static final Map<String, String> map = ImmutableMap.of(lang, translatedValueDescription);
	private static final String invalidfield = "invalidField";

	@Test
	public void forDescriptionFieldReturnsValidObject() {
		// given
		final Converter converter = org.cmdbuild.translation.legacy.MenuItemConverter //
				.of(field)//
				.withTranslations(map);

		// when
		final TranslationObject translationObject = converter //
				.withIdentifier(menuItemUuid) //
				.create();

		// then
		assertTrue(converter.isValid());
		assertNotNull(translationObject);
		assertTrue(MenuItemDescription.class.cast(translationObject).getName().equals(menuItemUuid));
		assertTrue(translationObject.getTranslations().get(lang).equals(translatedValueDescription));
	}

	@Test
	public void converterIsCaseInsensitiveForTheField() {
		// given
		final Converter converter = ClassConverter //
				.of(field) //
				.withIdentifier(menuItemUuid) //
				.withTranslations(map);

		// when
		final TranslationObject translationObject = converter.create();

		// then
		assertTrue(converter.isValid());
		assertNotNull(translationObject);
		assertTrue(ClassDescription.class.cast(translationObject).getName().equals(menuItemUuid));
		assertTrue(translationObject.getTranslations().get(lang).equals(translatedValueDescription));
	}

	@Test
	public void unsupportedFieldGeneratesNotValidConverter() {
		// given

		// when
		final ClassConverter converter = ClassConverter //
				.of(invalidfield);

		// then
		assertTrue(!converter.isValid());
	}

}
