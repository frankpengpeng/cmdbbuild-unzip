package org.cmdbuild.translation.legacy;

import java.util.Map;


public interface Converter {

	public boolean isValid();

	public TranslationObject create();

	public Converter withOwner(String parentIdentifier);

	public Converter withIdentifier(String identifier);

	public Converter withTranslations(Map<String, String> map);

}
