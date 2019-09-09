/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import com.google.common.base.Predicate;
import java.io.File;
import static java.lang.String.format;
import java.util.List;
import javax.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface PostgresHelper {

    String getServerVersion();

    long getDatabaseSize();

    boolean dumpContainsSchema(File dumpFile);

    List<String> getTablesInDump(File dumpFile);

    String runCommand(String command, List<String> params);

    String executeQuery(String query);

    void restoreDumpFromFile(File dumpFile, @Nullable Predicate<String> itemLineFilter);

    void dumpDatabaseToFile(File targetFile);

    default String executeQuery(String query, Object... params) {
        return executeQuery(format(query, params));
    }

    default String runCommand(String command, String... params) {
        return runCommand(command, list(params));
    }

    default void restoreDumpFromFile(File dumpFile) {
        restoreDumpFromFile(dumpFile, null);
    }

    void checkDumpFile(File file);

}
