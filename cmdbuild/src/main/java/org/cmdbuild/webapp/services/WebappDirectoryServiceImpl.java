/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.services;

import com.google.common.base.Supplier;
import java.io.File;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import java.util.List;
import javax.annotation.Nullable;
import javax.servlet.ServletContext;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.DirectoryServiceImpl;

@Component
public class WebappDirectoryServiceImpl extends DirectoryServiceImpl {

	public WebappDirectoryServiceImpl(ServletContext servletContext) {
		super(builder()
				.withContainerDirectory(getFileOrNullIfError("catalina base dir", () -> new File(checkNotBlank(System.getProperty("catalina.base")))))
				.withConfigDirectory(getFileOrNullIfError("cmdbuild config dir", () -> getConfigDirectory(servletContext)))
				.withWebappDirectory(getFileOrNullIfError("cmdbuild webapp dir", () -> getWebappRoot(servletContext))));
	}

	private static @Nullable
	File getFileOrNullIfError(String info, Supplier<File> supplier) {
		File file;
		try {
			file = supplier.get();
		} catch (Exception ex) {
			LoggerFactory.getLogger(WebappDirectoryServiceImpl.class).error("error configuring {}", info, ex);
			file = null;
		}
		return file;
	}

	public static File getConfigDirectory(ServletContext servletContext) {

		String cmdbuildContext = getWebappName(servletContext);

		List<String> candidatePaths = asList(
				servletContext.getInitParameter("configLocation"),//legacy param value
				servletContext.getInitParameter("org.cmdbuild.config.location"), //new param value
				format("%s/conf/%s", System.getProperty("catalina.base"), cmdbuildContext),
				servletContext.getRealPath("WEB-INF/conf"),
				getServletContextRootPath(servletContext) + "/WEB-INF/conf"); // this is required if WEB-INF/config is a symlink (for example when we emulate a cluster on a single host, and want to share config dir via symlink)

		for (String path : candidatePaths) {
			if (!isBlank(path)) {
				File file = new File(path);
				if (file.isDirectory()) {
					return file;
				}
			}
		}

		throw new RuntimeException("unable to find config directory");

	}

	public static File getConfigFile(ServletContext servletContext, String fileName) {
		return new File(getConfigDirectory(servletContext), fileName);
	}

	public static String getServletContextRootPath(ServletContext servletContext) {
		return servletContext.getRealPath("/");
	}

	public static File getWebappRoot(ServletContext servletContext) {
		return new File(getServletContextRootPath(servletContext));
	}

	public static String getWebappName(ServletContext servletContext) {
		return getWebappRoot(servletContext).getName();
	}

}
