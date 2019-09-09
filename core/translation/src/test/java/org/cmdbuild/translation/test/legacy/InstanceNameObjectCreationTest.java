package org.cmdbuild.translation.test.legacy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.cmdbuild.translation.legacy.TranslationObject;
import org.cmdbuild.translation.legacy.Converter;
import org.cmdbuild.translation.legacy.InstanceConverter;
import org.cmdbuild.translation.legacy.InstanceName;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class InstanceNameObjectCreationTest {

	private static final String lang = "it";
	private static final String translatedName = "demo-it";
	private static final Map<String, String> map = ImmutableMap.of(lang, translatedName);

	@Test
	public void forInstanceNameReturnsValidObject() {
		// given
		final Converter converter = InstanceConverter //
				.of(InstanceConverter.nameField())//
				.withTranslations(map);

		// when
		final TranslationObject translationObject = converter.create();

		// then
		assertTrue(converter.isValid());
		assertNotNull(translationObject);
		assertTrue(translationObject instanceof InstanceName);
		assertTrue(translationObject.getTranslations().get(lang).equals(translatedName));
	}

}
