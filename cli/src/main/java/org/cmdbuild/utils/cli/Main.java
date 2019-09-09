/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli;

import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.ext.spring.LogbackConfigurer;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import java.io.File;
import java.io.FileNotFoundException;
import static java.util.Arrays.asList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import static org.apache.commons.lang3.StringUtils.trim;
import org.apache.commons.lang3.time.StopWatch;
import org.cmdbuild.utils.cli.commands.BenchmarkCommandRunner;
import org.cmdbuild.utils.cli.commands.BimCommandRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.utils.cli.commands.CryptoCommandRunner;
import org.cmdbuild.utils.cli.commands.DbconfigCommandRunner;
import org.cmdbuild.utils.cli.commands.InstallCommandRunner;
import org.cmdbuild.utils.cli.commands.PostgresCommandRunner;
import org.cmdbuild.utils.cli.commands.RestCommandRunner;
import org.cmdbuild.utils.cli.commands.RiverCommandRunner;
import org.cmdbuild.utils.cli.commands.ShrkDbUtilsCommandRunner;
import org.cmdbuild.utils.cli.commands.SoapCommandRunner;
import org.cmdbuild.utils.cli.commands.TomcatCommandRunner;
import org.cmdbuild.utils.cli.commands.ToolsCommandRunner;
import org.cmdbuild.utils.cli.commands.UninstallCommandRunner;
import org.cmdbuild.utils.cli.commands.UpgradeCommandRunner;
import org.cmdbuild.utils.gui.GuiCommandRunner;
import static org.cmdbuild.utils.gui.GuiCommandRunner.canRunGui;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmZipUtils.dirToZip;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class Main {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static File cliHome;

    public static void main(String[] args) throws Exception {
        new Main().runMain(args);
    }

    private final Map<String, CliCommandRunner> commandRunners = uniqueIndex(asList(
            new PostgresCommandRunner(),
            new TomcatCommandRunner(),
            new DbconfigCommandRunner(),
            new ShrkDbUtilsCommandRunner(),
            new CryptoCommandRunner(),
            new SoapCommandRunner(),
            new InstallCommandRunner(),
            new UpgradeCommandRunner(),
            new UninstallCommandRunner(),
            new RestCommandRunner(),
            new RiverCommandRunner(),
            new BimCommandRunner(),
            new BenchmarkCommandRunner(),
            new GuiCommandRunner(),
            new ToolsCommandRunner()), CliCommandRunner::getName);

    private void runMain(String[] args) throws Exception {
        enableDefaultLogging();
        try {
            List<String> argList = list(args);
            boolean checkTime = false, printHelp = false;
            for (Iterator<String> iterator = argList.iterator(); iterator.hasNext();) {
                String key = iterator.next();
                if (key.toLowerCase().matches("-+(v|verbose)")) {
                    iterator.remove();
                    enableVerboseLogging();
                } else if (key.toLowerCase().matches("-+(t|time)")) {
                    iterator.remove();
                    checkTime = true;
                } else if (key.toLowerCase().matches("-+(h|help)")) {
                    iterator.remove();
                    printHelp = true;
                }
            }
            logger.debug("running from war {} = {}", getCliHome().isFile() ? "file" : "dir", getCliHome().getCanonicalPath());
            CliCommandRunner cliService;
            if (argList.isEmpty()) {
                if (!printHelp && canRunGui() && isRunningFromWarFile()) {
                    cliService = new GuiCommandRunner();
                } else {
                    printHelp();
                    return;
                }
            } else {
                cliService = commandRunners.get(trim(argList.get(0)).toLowerCase());
                checkNotNull(cliService, "%s is not a valid cli mode", argList.get(0));
                argList.remove(0);
            }
            if (printHelp) {
                argList.add("-h");
            }
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            cliService.exec(argList.toArray(new String[]{}));
            stopWatch.stop();
            if (checkTime) {
                System.err.printf("\ntotal time: %.3fs\n", stopWatch.getTime() / 1000d);
            }

        } catch (IllegalArgumentException ex) {
            logger.debug("error", ex);
            System.err.println("ERROR: " + ex.getMessage());
            printHelp();
            System.exit(1);
        }
    }

    private void printHelp() {
        Options options = new Options();
        options.addOption("h", "help", false, "print help");
        options.addOption("t", "time", false, "print total operation time");
        options.addOption("v", "verbose", false, "verbose output (enable debug logs)");
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getExecNameForHelpMessage(), options, true);
        commandRunners.values().forEach((cliService) -> {
            System.err.println("\t\t" + cliService.getName() + "\t" + cliService.getDescription());
        });
    }

    private void enableDefaultLogging() throws FileNotFoundException, JoranException {
        LogbackConfigurer.initLogging("classpath:cli_logback_default.xml");
    }

    private void enableVerboseLogging() throws FileNotFoundException, JoranException {
        LogbackConfigurer.initLogging("classpath:cli_logback_verbose.xml");
    }

    public static void setCliHome(File cliHome) {
        Main.cliHome = cliHome;
    }

    public static File getCliHome() {
        return checkNotNull(cliHome, "cli home is not available");
    }

    public static File getWarFile() {
        if (isRunningFromWarFile()) {
            return getCliHome();
        } else {
            checkArgument(isRunningFromWebappDir());
            File dir = tempDir();
            File warFile = new File(dir, "cmdbuild.war");
            byte[] zipData = dirToZip(getCliHome());
            writeToFile(zipData, warFile);
            return warFile;
        }
    }

    public static boolean isRunningFromWebappDir() {
        return cliHome != null && cliHome.isDirectory();
    }

    public static boolean isRunningFromWarFile() {
        return cliHome != null && cliHome.isFile();
    }

    public static String getExecNameForHelpMessage() {
        return "cmdbuild.<sh|bat>";
    }
}
