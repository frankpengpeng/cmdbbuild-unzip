/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tecnoteca.sencha.maven.plugin;

import java.io.File;
import java.io.IOException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "secha-workspace-init")
public class SenchaMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project.basedir}", readonly = true)
	private File basedir;

	@Override
	public void execute() throws MojoExecutionException {
		try {
			getLog().info("check project dir " + basedir.getAbsolutePath() + " for sencha cmd required dirs");

			File extDir = new File(basedir, "ext"), senchaDir = new File(basedir, ".sencha");

			if (!extDir.exists()) {
				getLog().info("unpack ext sdk");
				File zipFile = new File(basedir, "ext.zip");
				FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/ext.zip"), zipFile);
				new ZipFile(zipFile).extractAll(basedir.getAbsolutePath());
				FileUtils.deleteQuietly(zipFile);
			}

			if (!senchaDir.exists()) {
				getLog().info("unpack .sencha config dir");
				File zipFile = new File(basedir, "sencha.zip");
				FileUtils.copyInputStreamToFile(getClass().getResourceAsStream("/sencha.zip"), zipFile);
				new ZipFile(zipFile).extractAll(basedir.getAbsolutePath());
				FileUtils.deleteQuietly(zipFile);
			}

		} catch (IOException | ZipException ex) {
			throw new MojoExecutionException("error", ex);
		}
	}
}
