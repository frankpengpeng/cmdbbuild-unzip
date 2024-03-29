/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.utils.cli.CliCommandRunner;
import static org.cmdbuild.utils.cli.Main.getExecNameForHelpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCommandRunner implements CliCommandRunner {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final String name, description;

	public AbstractCommandRunner(String name, String description) {
		this.name = checkNotNull(trimToNull(name));
		this.description = checkNotNull(trimToNull(description));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	protected Options buildOptions() {
		Options options = new Options();
		options.addOption("h", "help", false, "print help");
		return options;
	}

	@Override
	public void exec(String[] args) throws Exception {
		try {
			Options options = buildOptions();
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp(getExecNameForHelpMessage() + " " + getName(), options, true);
				printAdditionalHelp();
			} else {
				exec(cmd);
			}
		} catch (ParseException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	protected void printAdditionalHelp() {

	}

	protected abstract void exec(CommandLine cmd) throws Exception;

	protected File getFile(CommandLine cmd, String key, boolean shouldExist, String errorMessage) throws Exception {
		String value = cmd.getOptionValue(key);
		checkArgument(!isBlank(value), errorMessage);
		File file = new File(value);
		if (shouldExist) {
			checkArgument(file.isFile(), errorMessage);
		} else {
			checkArgument(file.getParentFile().isDirectory(), errorMessage);
		}
		return file;
	}
}
