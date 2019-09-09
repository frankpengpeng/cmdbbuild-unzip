/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

import java.io.File;
import java.util.List;

/**
 * this service allows configuration of loggers (by category and level). Log
 * configuration is reloaded on write, so changes are immediately applied in the
 * system.
 *
 * @author davide
 */
public interface LoggersConfigService {

    List<File> getLogFiles();

    List<LoggerConfig> getAllLoggerConfig(); 
    void setLoggerConfig(LoggerConfig loggerConfig);

    void removeLoggerConfig(String category);

    String getConfigFileContent();
}
