/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import java.util.List;
import java.util.Map;

public interface ImportExportOperationResultError {

    long getRecordIndex();

    long getRecordLineNumber();

    List<Map.Entry<String, String>> getRecordData();

    String getUserErrorMessage();

    String getTechErrorMessage();

}
