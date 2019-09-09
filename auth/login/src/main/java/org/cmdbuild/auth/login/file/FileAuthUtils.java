/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.file;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import javax.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class FileAuthUtils {

	public static AuthFile buildAuthFile(File authDir) {
		checkArgument(authDir.exists() && authDir.isDirectory());
		String fileName = randomId();
		File file = new File(authDir, fileName);
		try {
			file.createNewFile();
		} catch (IOException ex) {
			throw runtime(ex);
		}
		checkArgument(file.exists());
		String password = format("file:%s", fileName);
		return new AuthFile() {
			@Override
			public File getFile() {
				return file;
			}

			@Override
			public String getPassword() {
				return password;
			}
		};
	}

	public static boolean isAuthFilePassword(@Nullable String password) {
		return isNotBlank(password) && password.matches("^file:.+");
	}

	public static boolean isValidAuthFilePassword(File authDir, String password) {
		checkArgument(isAuthFilePassword(password));
		String fileName = checkNotBlank(password.replaceFirst("^file:", ""));
		File file = new File(authDir, fileName);
		return file.exists();
	}

	public interface AuthFile {

		File getFile();

		String getPassword();
	}

}
