/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import javax.activation.DataSource;

public interface ImportExportProcessorService {

    DataSource exportDataWithTemplate(ImportExportTemplate template);

    ImportExportOperationResult importDataWithTemplate(DataSource data, ImportExportTemplate template);

}
