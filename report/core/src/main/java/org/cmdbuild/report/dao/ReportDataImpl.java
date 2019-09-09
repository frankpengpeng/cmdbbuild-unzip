package org.cmdbuild.report.dao;

import org.cmdbuild.report.ReportData;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import javax.annotation.Nullable;
import net.sf.jasperreports.engine.JasperReport;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.report.ReportInfo;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Report")
public class ReportDataImpl implements ReportData {

	private final Long id;
	private final String code;
	private final String description;
	private final boolean isActive;
	private final String query;
	private final byte[] mainReport;
	private final List<byte[]> subReports;
	private final List<byte[]> images;
	private final List<String> imageNames;
	private final List<JasperReport> jasperReports;
	private final JasperReport masterReport;

	private ReportDataImpl(ReportDataImplBuilder builder) {
		this.id = builder.id;
		this.code = checkNotBlank(builder.code, "report code cannot be null");
		this.description = nullToEmpty(builder.description);
		this.query = builder.query;
		this.mainReport = checkNotNull(builder.mainReport, "main report cannot be null");
		this.subReports = ImmutableList.copyOf(checkNotNull(builder.subReports, "sub reports array cannot be null"));
		this.images = ImmutableList.copyOf(checkNotNull(builder.images, "report images array cannot be null"));
		this.imageNames = ImmutableList.copyOf(checkNotNull(builder.imageNames, "report image names array cannot be null"));
		this.isActive = builder.isActive;
		jasperReports = subReports.stream().map((b) -> (JasperReport) deserializeObject(b)).collect(toImmutableList());
		masterReport = (JasperReport) deserializeObject(mainReport);
	}

	@Override
	@Nullable
	@CardAttr(ATTR_ID)
	public Long getId() {
		return id;
	}

	@Override
	@CardAttr(ATTR_CODE)
	public String getCode() {
		return code;
	}

	@Override
	@CardAttr(ATTR_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	@Override
	@CardAttr("Query")
	public String getQuery() {
		return query;
	}

	@Override
	@CardAttr("MainReport")
	public byte[] getMainReport() {
		return mainReport;
	}

	@Override
	@CardAttr("SubReports")
	public List<byte[]> getSubReports() {
		return subReports;
	}

	@Override
	public List<JasperReport> getRichReportsAsJasperReports() {
		return jasperReports;
	}

	@Override
	public JasperReport getMasterReportAsJasperReport() {
		return masterReport;
	}

	@Override
	@CardAttr("Images")
	public List<byte[]> getImages() {
		return images;
	}

	@Override
	@CardAttr("ImageNames")
	public List<String> getImageNames() {
		return imageNames;
	}

	@Override
	@CardAttr("Active")
	public boolean isActive() {
		return isActive;
	}

	@Override
	public String toString() {
		return "ReportData{" + "id=" + id + ", code=" + code + '}';
	}

	public static ReportDataImplBuilder builder() {
		return new ReportDataImplBuilder();
	}

	public static ReportDataImplBuilder copyOf(ReportInfo source) {
		return new ReportDataImplBuilder().withInfo(source);

	}

	public static ReportDataImplBuilder copyOf(ReportData source) {
		return new ReportDataImplBuilder().withInfo(source)
				.withQuery(source.getQuery())
				.withMainReport(source.getMainReport())
				.withSubReports(source.getSubReports())
				.withImages(source.getImages())
				.withImageNames(source.getImageNames());

	}

	public static class ReportDataImplBuilder implements Builder<ReportDataImpl, ReportDataImplBuilder> {

		private Long id;
		private String code;
		private String description;
		private String query;
		private byte[] mainReport;
		private List<byte[]> subReports;
		private List<byte[]> images;
		private List<String> imageNames;
		private Boolean isActive;

		public ReportDataImplBuilder withInfo(ReportInfo info) {
			return this
					.withId(info.getId())
					.withCode(info.getCode())
					.withDescription(info.getDescription())
					.withActive(info.isActive());
		}

		public ReportDataImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public ReportDataImplBuilder withCode(String code) {
			this.code = code;
			return this;
		}

		public ReportDataImplBuilder withDescription(String description) {
			this.description = description;
			return this;
		}

		public ReportDataImplBuilder withQuery(String query) {
			this.query = query;
			return this;
		}

		public ReportDataImplBuilder withMainReport(byte[] mainReport) {
			this.mainReport = mainReport;
			return this;
		}

		public ReportDataImplBuilder withSubReports(List<byte[]> richReports) {
			this.subReports = richReports;
			return this;
		}

		public ReportDataImplBuilder withImages(List<byte[]> images) {
			this.images = images;
			return this;
		}

		public ReportDataImplBuilder withImageNames(List<String> imagesName) {
			this.imageNames = imagesName;
			return this;
		}

		public ReportDataImplBuilder withActive(Boolean isActive) {
			this.isActive = isActive;
			return this;
		}

		@Override
		public ReportDataImpl build() {
			return new ReportDataImpl(this);
		}

	}
}
