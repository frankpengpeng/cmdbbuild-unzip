/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.maven;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import javax.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenCommandLineBuilder;
import org.apache.maven.shared.invoker.MavenInvocationException;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author davide
 */
public class MavenUtils {

	private final static Logger logger = LoggerFactory.getLogger(MavenUtils.class);

	public static File getFileByArtifactId(String artifactId) throws IOException {
		logger.info("fetch artifact = {}", artifactId);
		File tempDir = tempDir("artifact");
		mavenInvoke("org.apache.maven.plugins:maven-dependency-plugin:2.8:get", ImmutableMap.of("artifact", artifactId));
		mavenInvoke("org.apache.maven.plugins:maven-dependency-plugin:2.8:copy", ImmutableMap.of("artifact", artifactId, "outputDirectory", tempDir.getAbsolutePath()));
		File file = Iterables.getOnlyElement(Arrays.asList(tempDir.listFiles()));
		checkArgument(file.exists() && file.isFile());
		return file;
	}

	/**
	 * remove a file obtained by {@link #getFileByArtifactId(java.lang.String) }
	 * and all related resources.
	 *
	 * Since {@link #getFileByArtifactId(java.lang.String) } currentlu creates a
	 * new directory, and the file in it, this method is responsible to clean
	 * the directory as well as the file. Implementation may change in the
	 * future.
	 *
	 * @param file
	 */
	public static void cleanupFileObtainedByArtifactId(File file) {
		FileUtils.deleteQuietly(file.getParentFile());
	}

	public static void mavenInvoke(String goal, @Nullable Map<String, String> params) {
		findMaven();
		try {
			logger.info("invoke maven {} {}", goal, firstNonNull(params, ""));
			InvocationRequest request = new DefaultInvocationRequest();
			request.setBatchMode(true);
			request.setGoals(Collections.singletonList(goal));
			Properties properties = new Properties();
			if (params != null) {
				properties.putAll(params);
			}
			request.setProperties(properties);
			request.setMavenOpts("-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN");
			Invoker invoker = new DefaultInvoker();
			InvocationResult invocationResult = invoker.execute(request);
			checkArgument(invocationResult.getExitCode() == 0, "maven invocation failed");
		} catch (MavenInvocationException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void checkMaven() {
		findMaven();
	}

	private static void findMaven() {
		try {
			new MavenCommandLineBuilder() {
				{
					try {
						checkRequiredState();
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				}
			};
		} catch (Exception ex) {
			logger.warn("maven not found, trying to auto configure: {}", ex.toString());
			try {
				ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", "which mvn").redirectErrorStream(true);
				Process process = processBuilder.start();
				String res = trimToNull(IOUtils.toString(process.getInputStream()));
				process.waitFor();
				checkNotNull(res);
				Path mvnPath = Paths.get(res).toRealPath();
				String mavenHome = mvnPath.getParent().getParent().toAbsolutePath().toString();
				logger.info("set maven.home to {}", mavenHome);
				System.setProperty("maven.home", mavenHome);
			} catch (Exception exx) {
				logger.error("error", exx);
				throw new IllegalArgumentException("unable to find maven, set M2_HOME or maven.home");
			}
		}
	}
}
