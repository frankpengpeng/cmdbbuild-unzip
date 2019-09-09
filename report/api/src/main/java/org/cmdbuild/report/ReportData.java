package org.cmdbuild.report;

import java.util.List;
import javax.annotation.Nullable;

import net.sf.jasperreports.engine.JasperReport;

public interface ReportData extends ReportInfo {

	@Nullable
	@Override
	Long getId();

	@Override
	String getCode();

	@Override
	String getDescription();

	String getQuery();

	List<byte[]> getSubReports();

	List<JasperReport> getRichReportsAsJasperReports();

	byte[] getMainReport();

	List<byte[]> getImages();

	List<String> getImageNames();

	JasperReport getMasterReportAsJasperReport();

}
