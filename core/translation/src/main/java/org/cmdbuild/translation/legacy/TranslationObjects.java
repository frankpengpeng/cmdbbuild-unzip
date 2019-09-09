package org.cmdbuild.translation.legacy;

public class TranslationObjects {

	public static final TranslationObject NULL_TRANSLATION_OBJECT = new NullTranslationObject();

	public static TranslationObject nullTranslationObject() {
		return NULL_TRANSLATION_OBJECT;
	}

	private TranslationObjects() {
		// prevents instantiation
	}

}
