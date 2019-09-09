package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.Rule;
import org.passay.RuleResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;

@Component
public class PasswordManagementServiceImpl implements PasswordManagementService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

//	//TODO: do not copy config via setter, use PasswordManagementConfiguration directly in here
//	protected static boolean isPasswordManagementEnabled = false;
//	private PasswordValidator validator;
//	protected static Integer maxPasswordAgeDays;
//
//	protected static Boolean differFromPrevious;
//	protected static Boolean differFromUserName;
//
//	protected Integer passwordMinLenght;
//	protected Integer forewarningDays;
//
//	protected Boolean requireDigit;
//	protected Boolean requireUppercase;
//	protected Boolean requireLowerCase;
	private static final int MAX_PASSWORD_LENGTH = 65000;

	private final PasswordManagementConfiguration config;

	public PasswordManagementServiceImpl(PasswordManagementConfiguration configuration) {
		this.config = checkNotNull(configuration);
	}

//	protected PasswordManagementService passwordManagementService() {
//
//		//TODO: do not copy config via setter, use PasswordManagementConfiguration directly in PasswordManagementService
//		PasswordManagementService pm = new PasswordManagementService();
//
//		if (config.isPasswordValidationEnabled()) {
//			pm.setPaswordValidationEnabled(true); //defaults to false
//		}
//		pm.setPasswordMinLength(config.getPasswordMinLength());
//		pm.setMaxPasswordAgeDays(config.getMaxPasswordAgeDays());
//		pm.setForewarningDays(config.getForewarningDays());
//
//		pm.setDifferFromUserName(config.getDifferentFromUsername());
//		pm.setDifferFromPrevious(config.getDifferentFromPrevious());
//		pm.setRequireDigit(config.requireDigit());
//		pm.setRequireLowerCase(config.requireLowercase());
//		pm.setRequireUppercase(config.requireUppercase());
//
//		//PasswordManagement::init is called before Component is initialized, so the bean can start with full init
//		//This is not a strict requirement
//		pm.init();
//		return pm;
//	}
	/**
	 * Set up of operative policy rules according to injected configuration.
	 * Should be called as soon as possible, better if before Bean instantiation
	 */
	private PasswordValidator getPasswordValidator() {

		logger.info("PasswordManagerService init invoked. Building passay validator according to policy...");
		List<Rule> rules = new ArrayList<>();

		if (config.getPasswordMinLength() > 0) {
			rules.add(new LengthRule(config.getPasswordMinLength(), MAX_PASSWORD_LENGTH));
		}
		if (config.requireDigit()) {
			rules.add(new CharacterRule(EnglishCharacterData.Digit));
		}
		if (config.requireLowercase()) {
			rules.add(new CharacterRule(EnglishCharacterData.LowerCase));
		}
		if (config.requireUppercase()) {
			rules.add(new CharacterRule(EnglishCharacterData.UpperCase));
		}

		return new PasswordValidator(rules);

	}

	/**
	 * @return true if password management is enabled
	 */
	@Override
	public boolean isPasswordManagementEnabled() {
		return config.isPasswordValidationEnabled();
	}

	/**
	 * @return expiration date of a changed password according to current policy
	 * if max password age in days is null or <= 0 then no expiration date is
	 * computed (null returned)
	 */
	@Override
	public LocalDateTime getPasswordExpiration() {
		if (config.getMaxPasswordAgeDays() <= 0 || !isPasswordManagementEnabled()) {
			return null;
		}
		return (LocalDateTime.now().plusDays(config.getMaxPasswordAgeDays()).withHour(23).withMinute(59).withSecond(59));
	}

	/**
	 * @param newPassword
	 * @param username username or null if you don't want validation against
	 * differ-from-username rule
	 * @param newPasswordOrHash new password or hashed new password if hashing
	 * is used to save passwords
	 * @param previousPasswordOrHash previous password or hashed previous
	 * password if hashing is used
	 * @return true if password is valid, false otherwise
	 */
	@Override
	public boolean isValidPassword(String newPassword, String username, String newPasswordOrHash, String previousPasswordOrHash) {

		if (!isPasswordManagementEnabled()) {
			return true;
		}
		if (config.getDifferentFromUsername()) {
			if (username == null || newPassword == null) {
				return false;
			}
			if (newPassword.toLowerCase().contains(username.toLowerCase())) {
				return false;
			}
		}
		if (config.getDifferentFromPrevious()) {
			if (previousPasswordOrHash == null || newPasswordOrHash == null || previousPasswordOrHash.equals(newPasswordOrHash)) {
				return false;
			}
		}

		PasswordData passwordData = new PasswordData(newPassword);
		RuleResult validationResult = getPasswordValidator().validate(passwordData);
		return validationResult.isValid();

	}

	/**
	 * @param password
	 * @return true if password is valid, false otherwise
	 */
	@Override
	public boolean isValidPassword(String password) {

		if (!isPasswordManagementEnabled()) {
			return true;
		}
		PasswordData passwordData = new PasswordData(password);
		RuleResult validationResult = getPasswordValidator().validate(passwordData);
		return validationResult.isValid();
	}

//	@Scheduled(initialDelay = 100000 , fixedDelay = 60000L * 60 * 8)
	//FIXME schedule period after debug
//	@Scheduled(initialDelay = 15000 , fixedDelay = 60000L * 60 * 8 )
//	public void sendMail4PasswordsAboutToExpire() {
//		logger.info("sendMail4PasswordsAboutToExpire invoked");
//	}
}
