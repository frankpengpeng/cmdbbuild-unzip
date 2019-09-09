/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;

public class DirectoryServiceImpl implements DirectoryService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final File webappDirectory, configDirectory, containerDirectory;

	public DirectoryServiceImpl(DirectoryServiceImplBuilder builder) {
		this.webappDirectory = builder.webappDirectory;
		this.configDirectory = builder.configDirectory;
		this.containerDirectory = builder.containerDirectory;
		logger.info("cmdbuild webapp dir = {}", webappDirectory);
		logger.info("cmdbuild config dir = {}", configDirectory);
		logger.info("container dir = {}", containerDirectory);
	}

	@Override
	public File getConfigDirectory() {
		return checkNotNull(configDirectory, "config directory not available (check startup logs for errors)");
	}

	@Override
	public File getWebappDirectory() {
		return checkNotNull(webappDirectory, "root directory not available (check startup logs for errors)");
	}

	@Override
	public File getContainerDirectory() {
		return checkNotNull(containerDirectory, "container directory not available (check startup logs for errors)");
	}

	@Override
	public boolean hasConfigDirectory() {
		return configDirectory != null;
	}

	@Override
	public boolean hasWebappDirectory() {
		return webappDirectory != null;
	}

	@Override
	public boolean hasContainerDirectory() {
		return containerDirectory != null;
	}

	public static DirectoryServiceImplBuilder builder() {
		return new DirectoryServiceImplBuilder();
	}

//    public static DirectoryServiceImplBuilder copyOf(DirectoryServiceImpl source) {
//        return new DirectoryServiceImplBuilder() 
//        .withWebappDirectory(source.getWebappDirectory())
//        .withConfigDirectory(source.getConfigDirectory())
//        .withContainerDirectory(source.getContainerDirectory());
//    }
	public static class DirectoryServiceImplBuilder implements Builder<DirectoryServiceImpl, DirectoryServiceImplBuilder> {

		private File webappDirectory;
		private File configDirectory;
		private File containerDirectory;

		public DirectoryServiceImplBuilder withWebappDirectory(File webappDirectory) {
			this.webappDirectory = webappDirectory;
			return this;
		}

		public DirectoryServiceImplBuilder withConfigDirectory(File configDirectory) {
			this.configDirectory = configDirectory;
			return this;
		}

		public DirectoryServiceImplBuilder withContainerDirectory(File containerDirectory) {
			this.containerDirectory = containerDirectory;
			return this;
		}

		@Override
		public DirectoryServiceImpl build() {
			return new DirectoryServiceImpl(this);
		}

	}
}
