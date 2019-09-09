/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.commands;

import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.codec.binary.Base64;
import static org.cmdbuild.etl.utils.XlsProcessingUtils.getRecordsFromXlsFile;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliAction;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommand;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandParser;
import org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandUtils;
import static org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandUtils.prepareAction;
import static org.cmdbuild.utils.cli.commands.restcommandutils.CliCommandParser.printActionHelp;
import static org.cmdbuild.utils.cli.utils.CliUtils.hasInteractiveConsole;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.encode.CmPackUtils.packBytes;
import static org.cmdbuild.utils.encode.CmPackUtils.packString;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackString;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class ToolsCommandRunner extends AbstractCommandRunner {

    private final Map<String, CliAction> actions;

    public ToolsCommandRunner() {
        super("tools", "mixed tools");
        actions = new CliCommandParser().parseActions(this);
    }

    @Override
    protected Options buildOptions() {
        Options options = super.buildOptions();
        return options;
    }

    @Override
    protected void printAdditionalHelp() {
        System.out.println("\navailable methods:");
        printActionHelp(actions);
    }

    @Override
    protected void exec(CommandLine cmd) throws Exception {
        Iterator<String> iterator = cmd.getArgList().iterator();
        if (!iterator.hasNext()) {
            System.out.println("no rest call requested, doing nothing...");
        } else {
            CliCommandUtils.ExecutableAction action = prepareAction(actions, iterator);
            action.execute();
        }
    }

    @CliCommand
    protected void analyzeXls(String xlsFile) {
        File file = new File(xlsFile);
        AtomicInteger index = new AtomicInteger(0);
        getRecordsFromXlsFile(newDataSource(file)).forEach(l -> {
            System.out.printf("\n\n=== row %4s ===\n", index.get());
            AtomicInteger columnIndex = new AtomicInteger(0);
            l.forEach(c -> {
                String val;
                if (c instanceof Date) {
                    val = toIsoDateTimeUtc(c);
                } else {
                    val = toStringOrNull(c);
                }
                System.out.printf("%4s.%s: %20s  %s\n", index.get(), Integer.toString(columnIndex.getAndIncrement() + 10, Character.MAX_RADIX).toUpperCase(), format("(%s)", getClassOfNullable(c).getName()), val);
            });
            index.incrementAndGet();
        });
    }

    @CliCommand
    protected void base64encode(String payload) {
        String value = Base64.encodeBase64String(payload.getBytes());
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void base64encode() {
        byte[] data = toByteArray(System.in);
        String value = Base64.encodeBase64String(data);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void pack(String payload) {
        String value = packString(payload);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void pack() {
        byte[] data = toByteArray(System.in);
        String value = packBytes(data);
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", value);
        } else {
            System.out.print(value);
        }
    }

    @CliCommand
    protected void unpack(String value) throws IOException {
        if (hasInteractiveConsole()) {
            System.out.printf("=== BEGIN ===\n%s\n===  END  ===\n", unpackString(value));
        } else {
            System.out.write(unpackBytes(value));
        }
    }

}
