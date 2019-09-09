/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.exec;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.transform;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmProcessUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String executeProcess(Object... params) {
        return executeProcess(list(params));
    }

    public static String executeProcess(List<?> params) {
        ExecutorService executorService = Executors.newSingleThreadExecutor(namedThreadFactory(CmProcessUtils.class));
        try {
            String cliStr = Joiner.on(" ").join(params);
            LOGGER.debug("exec command = '{}'", cliStr);
            ProcessBuilder processBuilder = new ProcessBuilder(transform(params, CmStringUtils::toStringOrEmpty).toArray(new String[]{}));
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            AtomicReference<String> output = new AtomicReference<>("<output unavailable>");
            executorService.submit(() -> {
                try {
                    output.set(IOUtils.toString(process.getInputStream()));
                } catch (Exception ex) {
                    LOGGER.warn("error processing stream output from command = '{}'", ex, cliStr);
                }
            });
            int res = process.waitFor();
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
            LOGGER.debug("command = '{}' returned with code = {} and output = \n\n{}\n", cliStr, res, output.get());
            checkArgument(res == 0, "error executing command = '%s' : %s", cliStr, abbreviate(output.get()));
            return output.get();
        } catch (IOException | InterruptedException ex) {
            throw runtime(ex);
        } finally {
            shutdownQuietly(executorService);
        }
    }

    public static String executeBashScript(String bashScriptContent, Object... params) {
        return executeBashScript(bashScriptContent, list(params));
    }

    public static String executeBashScript(String bashScriptContent, List<Object> params) {
        File tempFile = tempFile(null, ".sh");
        try {
            writeToFile(bashScriptContent, tempFile);
//            return executeProcess(listOf(Object.class).with("/bin/bash", "-l", tempFile.getAbsolutePath()).with(params));
            return executeProcess(listOf(Object.class).with("/bin/bash", tempFile.getAbsolutePath()).with(params));
        } finally {
            deleteQuietly(tempFile);
        }
    }
}
