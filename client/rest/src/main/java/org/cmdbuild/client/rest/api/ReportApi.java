/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

public interface ReportApi {

	default ReportData executeAndDownload(String reportId, ReportFormat ext) {
		return executeAndDownload(reportId, ext, emptyMap());
	}

	ReportData executeAndDownload(String reportId, ReportFormat ext, Map<String, Object> params);

	void uploadReportTemplate(long reportId, List<Pair<String, byte[]>> files);

	default void uploadReportTemplate(long reportId, Collection<File> files) {
		uploadReportTemplate(reportId, files.stream().map((f) -> Pair.of(f.getName(), toByteArray(f))).collect(toList()));
	}

	ReportInfo createReport(ReportInfo reportInfo, List<Pair<String, byte[]>> files);

	default ReportInfo createReport(ReportInfo reportInfo, Collection<File> files) {
		return createReport(reportInfo, files.stream().map((f) -> Pair.of(f.getName(), toByteArray(f))).collect(toList()));
	}

	interface ReportData {

		byte[] toByteArray();
	}

}
