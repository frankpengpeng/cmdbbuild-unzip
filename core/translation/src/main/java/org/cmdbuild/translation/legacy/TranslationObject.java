package org.cmdbuild.translation.legacy;

import java.util.Map;

public interface TranslationObject {

	void accept(TranslationObjectVisitor visitor);

	Map<String, String> getTranslations();

	boolean isValid();

}
