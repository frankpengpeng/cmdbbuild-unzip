/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.util.Iterator;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import static org.cmdbuild.bim.utils.BimConfigUtils.bimserverConfigToSysConfig;
import org.cmdbuild.utils.bimserver.BimserverConfig;
import static org.cmdbuild.utils.bimserver.BimServerUtils.createAndStartBimserver;
import static org.cmdbuild.utils.bimserver.BimServerUtils.stopAndDestroyBimserver;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliAction;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommand;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandParser;
import static org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandUtils.executeAction;
import static org.cmdbuild.utils.io.CmPropertyUtils.serializeMapAsProperties;
import static org.cmdbuild.utils.url.CmUrlUtils.encodeUrlParams;

public class BimCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public BimCommandRunner() {
        super("bim", "bimserver utils");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        super.printAdditionalHelp();
        System.out.println("\navailable bim utils:");
        actions.values().stream().distinct().forEach((action -> {
            System.out.printf("\t%-32s\t%s\n", action.getHelpAliases(), action.getHelpParameters());
        }));
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no method selected, doing nothing...");
        } else {
            executeAction(actions, iterator);
        }
    }

    @CliCommand
    protected void install(String location) {
        System.out.printf("install bimserver to dir = %s\n", location);
        BimserverConfig config = createAndStartBimserver(location);
        System.out.printf("bimserver created and started, cmdbuild sys config: \n\n%s\n\ncmdbuild restws setconfigs '%s'\n", serializeMapAsProperties(bimserverConfigToSysConfig(config)), encodeUrlParams(bimserverConfigToSysConfig(config)));
    }

    @CliCommand
    protected void uninstall(String location) {
        System.out.printf("uninstall bimserver from dir = %s ... ", location);
        stopAndDestroyBimserver(location);
        System.out.println("done");
    }

}
