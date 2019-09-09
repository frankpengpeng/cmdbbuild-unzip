package org.cmdbuild.test.web.utils;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Nonnull;

//TODO: make configurable
public class FontAwesomeUtils {
	
	public static boolean isNotFontAwesomeIconModifier(@Nonnull String cssClass) {
		return ! (isFontAwesomeIconModifier(cssClass) || isFontAwesomeParametricModifier(cssClass));
	}
	
	public static final String ICON_FOLDER = "fa-folder";
	public static final String ICON_CLASS = "fa-file-text-o";
	public static final String ICON_PROCESS = "fa-cog"; //maybe subject to change as of 2018-04
	public static final String ICON_VIEW = "fa-table"; 
	public static final String ICON_CUSTOMPAGE = "fa-code"; 
	public static final String ICON_DASHBOARD = "fa-pie-chart";
	public static final String ICON_REPORT = "fa-file-o";
	public static final String ICON_REPORTPDF = "fa-file-pdf-o";
	
	
	private static final List<String> fontAwesomeIconModifiers = Arrays.asList(
			"fa-lg" , "fa-2x" , "fa-3x" , "fa-4x" , "fa-5x", "fa-inverse" , "fa-stack" , "fa-pulse" , "fa-spin" ,
			"fa-border" , "fa-fw" , "fa-ul" , "fa-li" , "fa-pull-left" , "fa-pull-right" 
			);
	
	private static final List<String> fontAwesomeParametricIconModifiers = Arrays.asList(
			"fa-stack-" ,  "fa-flip-" , "fa-rotate-" 
			);
	
	private static boolean isFontAwesomeParametricModifier(@Nonnull String cssClass) {
		return fontAwesomeParametricIconModifiers.stream().anyMatch(m -> cssClass.startsWith(m));
	}


	private static boolean isFontAwesomeIconModifier(String cssClass) {
		if (fontAwesomeIconModifiers.contains(cssClass))
			return true;
		return false;
	}

}
