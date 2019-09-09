/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import java.util.Map;
import javax.activation.DataHandler;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportFormat;

public interface ReportProcessorService {

    DataHandler executeReport(ReportData report, ReportFormat reportExtension, Map<String, Object> parameters);

}
