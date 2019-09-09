package org.cmdbuild.auth.login.file;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isAuthFilePassword;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isValidAuthFilePassword;
import org.cmdbuild.config.api.DirectoryService;

@Component
public class FileAuthenticator implements PasswordAuthenticator {

	private final DirectoryService directoryService;

	public FileAuthenticator(DirectoryService directoryService) {
		this.directoryService = checkNotNull(directoryService);
	}

	@Override
	public boolean isPasswordValid(LoginUserIdentity login, String password) {
		return isAuthFilePassword(password) && directoryService.hasContainerDirectory() && isValidAuthFilePassword(new File(directoryService.getContainerDirectory(), "temp/"), password);
	}

	@Override
	public String getName() {
		return "file";
	}

}
