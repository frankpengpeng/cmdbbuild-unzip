package org.cmdbuild.config;

import org.cmdbuild.auth.login.PasswordManagementConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.password")
public class PasswordManagementConfigurationImpl implements PasswordManagementConfiguration {

	@ConfigValue(key = "enable-password-change-management", defaultValue = FALSE)
	private boolean passwordValidationEnabled;

	@ConfigValue(key = "differ-from-username", defaultValue = TRUE)
	private boolean differentFromUsername;

	@ConfigValue(key = "differ-from-previous", defaultValue = TRUE)
	private boolean differentFromPrevious;

	@ConfigValue(key = "require-digit", defaultValue = FALSE)
	private boolean requireDigit;

	@ConfigValue(key = "require-lowercase", defaultValue = FALSE)
	private boolean requireLowercase;

	@ConfigValue(key = "require-uppercase", defaultValue = FALSE)
	private boolean requireUppercase;

	@ConfigValue(key = "min-length", defaultValue = "6")
	private int passwordMinLength;

	@ConfigValue(key = "max-password-age-days", defaultValue = "365")
	private int maxPasswordAgeDays;

	@ConfigValue(key = "forewarning-days", defaultValue = "7")
	private int forewarningDays;

	@Override
	public boolean isPasswordValidationEnabled() {
		return passwordValidationEnabled;
	}

	@Override
	public boolean getDifferentFromUsername() {
		return differentFromUsername;
	}

	@Override
	public boolean getDifferentFromPrevious() {
		return differentFromPrevious;
	}

	@Override
	public boolean requireDigit() {
		return requireDigit;
	}

	@Override
	public boolean requireLowercase() {
		return requireLowercase;
	}

	@Override
	public boolean requireUppercase() {
		return requireUppercase;
	}

	@Override
	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	@Override
	public int getMaxPasswordAgeDays() {
		return maxPasswordAgeDays;
	}

	@Override
	public int getForewarningDays() {
		return forewarningDays;
	}

}
