package org.cmdbuild.test.web.utils;

//TODO check if better CMDBuild Exceptions integration is needed
public class WebUICMDBuidTestException extends RuntimeException {

	public WebUICMDBuidTestException() {
		super(PREFIX);
	}
	
	public WebUICMDBuidTestException(String message) {
		super(PREFIX + message);
	}
	
	protected static String PREFIX = "WebUI Test Exception: ";
	
	public static String MESSAGE_DEFAULT = "no message";
	public static String MESSAGE_NO_NAMED_BUTTON_FOUND = "No enabled and displayed button named '";
	public static String MESSAGE_MORE_THAN_ONE_NAMED_BUTTON_FOUND = "More than one button named '";
	public static String MESSAGE_NO_ADMIN_SWITCH_OPTIO_FOUND = "No admin option found in dropdown '";

}
