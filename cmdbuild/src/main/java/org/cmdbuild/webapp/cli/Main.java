/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 */
public class Main {

	public static void main(String[] args) throws Throwable {
		File cliHome;
		File libDir;
		if (args.length >= 2 && "startedFromExplodedWar".equals(args[0])) {
			cliHome = new File(args[1]);
			libDir = null;
			args = Arrays.copyOfRange(args, 2, args.length);
		} else {
			URL main = Main.class.getResource(Main.class.getSimpleName() + ".class");
			String filename = main.toString().replaceFirst("jar:file:(.*[.][wj]ar)[!]/.*class", "$1");
			cliHome = new File(filename);
			String info = cliHome.getName() + "|" + cliHome.length() + "|" + cliHome.lastModified();
			File tempFile;
			for (int i = 0; true; i++) {
				tempFile = new File(System.getProperty("java.io.tmpdir"), "cm_" + UUID.nameUUIDFromBytes((info + i).getBytes()).toString().toLowerCase().replaceAll("[^a-z0-9]", ""));
				if (!tempFile.exists() || new File(tempFile, "ok").exists()) {
					break;
				} else {
					System.err.println("warning: broken temp dir " + tempFile.getAbsolutePath() + ", using next temp dir");
				}
			}
			if (!tempFile.exists()) {
				System.out.print("loading CMDBuild CLI from file " + cliHome.getName() + ", please wait...");
				tempFile.mkdirs();
				ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(cliHome));
				ZipEntry nextEntry;
				byte[] buffer = new byte[1024 * 1024];
				int i = 0;
				while ((nextEntry = zipInputStream.getNextEntry()) != null) {
					if (nextEntry.getName().endsWith(".jar") || nextEntry.getName().matches(".*.jar_disabled.*")) {
						File libFile = new File(tempFile, new File(nextEntry.getName()).getName().replaceFirst("_disabled.*", "")); //TODO change this, make it work without having to copy all jars to temp dir every time
						try (FileOutputStream out = new FileOutputStream(libFile)) {
							int count;
							while ((count = zipInputStream.read(buffer)) >= 0) {
								out.write(buffer, 0, count);
							}
						}
						if ((i++) % 3 == 0) {
							System.out.print(".");
						}
					}
				}
				new File(tempFile, "ok").createNewFile();
				System.out.println(" ready\n");
				libDir = tempFile;
			} else {
				libDir = tempFile;
			}

		}
		runCli(cliHome, libDir, args);
	}

	private static void runCli(File cliHome, /* Nullable */ File libDir, String[] args) throws Throwable {
		ClassLoader classLoader;
		if (libDir != null) {
			List<URL> urls = new ArrayList<>();

			for (File libFile : libDir.listFiles()) {
				urls.add(libFile.toURI().toURL());
			}
			URLClassLoader myClassLoader = new URLClassLoader(urls.toArray(new URL[]{}), Thread.currentThread().getContextClassLoader());

			Thread.currentThread().setContextClassLoader(myClassLoader);
			classLoader = myClassLoader;
		} else {
			classLoader = Thread.currentThread().getContextClassLoader();
		}

		Class<?> mainClass = classLoader.loadClass("org.cmdbuild.utils.cli.Main");
		mainClass.getMethod("setCliHome", File.class).invoke(null, cliHome);
		try {
			mainClass.getMethod("main", String[].class).invoke(null, (Object) args);
		} catch (InvocationTargetException ex) {
			throw ex.getCause();
		}
	}
}
