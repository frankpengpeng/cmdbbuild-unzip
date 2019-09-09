package org.cmdbuild.translation.test.legacy;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.cmdbuild.translation.legacy.TranslationObject;
import org.cmdbuild.translation.legacy.Converter;
import org.cmdbuild.translation.legacy.WidgetConverter;
import org.cmdbuild.translation.legacy.WidgetLabel;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class WidgetDescriptionObjectCreationTest {

	private static final String widgetId = "12345";
	private static final String lang = "it";
	private static final String translatedLabel = "etichetta";
	private static final Map<String, String> map = ImmutableMap.of(lang, translatedLabel);

	@Test
	public void forLabelReturnsValidObject() {
		// given
		final Converter converter = WidgetConverter //
				.of(WidgetConverter.label())//
				.withIdentifier(widgetId) //
				.withTranslations(map);

		// when
		final TranslationObject translationObject = converter.create();

		// then
		assertTrue(converter.isValid());
		assertNotNull(translationObject);
		assertTrue(WidgetLabel.class.cast(translationObject).getName().equals(widgetId));
		assertTrue(translationObject.getTranslations().get(lang).equals(translatedLabel));
	}

}
