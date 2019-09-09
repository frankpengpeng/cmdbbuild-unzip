package org.cmdbuild.report.dao;

import org.cmdbuild.report.ReportData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface ReportRepository {

	List<ReportData> getAllActiveReports();

	@Nullable
	ReportData getByCodeOrNull(String code);

	@Nullable
	ReportData getByIdOrNull(long id);

	void deleteReportById(long id);

	ReportData createReport(ReportData report);

	ReportData updateReport(ReportData report);

	default ReportData getById(long id) {
		return checkNotNull(getByIdOrNull(id), "report not found for id = %s", id);
	}

	default ReportData getReportByCode(String code) {
		return checkNotNull(getByCodeOrNull(code), "report not found for code = %s", code);
	}
}
