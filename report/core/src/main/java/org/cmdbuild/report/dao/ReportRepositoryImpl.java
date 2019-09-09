package org.cmdbuild.report.dao;

import org.cmdbuild.report.ReportData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;

@Component
public class ReportRepositoryImpl implements ReportRepository {

	private final DaoService dao;
	private final CmCache<Optional<ReportData>> reportsByCode;
	private final CmCache<Optional<ReportData>> reportsById;
	private final Holder<List<ReportData>> reportDataCache;

	public ReportRepositoryImpl(CacheService cacheService, DaoService dao) {
		this.dao = checkNotNull(dao);
		reportsById = cacheService.newCache("reports_by_id");
		reportsByCode = cacheService.newCache("reports_by_code");
		reportDataCache = cacheService.newHolder("reports_all");
	}

	private void invalidateCache() {
		reportsById.invalidateAll();
		reportsByCode.invalidateAll();
		reportDataCache.invalidate();
	}

	@Override
	public List<ReportData> getAllActiveReports() {
		return reportDataCache.get(() -> dao.selectAll().from(ReportDataImpl.class).asList());
	}

	@Override
	public @Nullable
	ReportData getByCodeOrNull(String code) {
		checkNotBlank(code);
		return reportsByCode.get(code, () -> Optional.ofNullable(dao.selectAll().from(ReportDataImpl.class).where(ATTR_CODE, EQ, code).getOneOrNull())).orElse(null);
	}

	@Override
	public @Nullable
	ReportData getByIdOrNull(long id) {
		return reportsById.get(String.valueOf(id), () -> Optional.ofNullable(dao.selectAll().from(ReportDataImpl.class).where(ATTR_ID, EQ, id).getOneOrNull())).orElse(null);
	}

	@Override
	public void deleteReportById(long id) {
		dao.delete(ReportDataImpl.class, id);
		invalidateCache();
	}

	@Override
	public ReportData createReport(ReportData report) {
		report = dao.create(report);
		invalidateCache();
		return report;
	}

	@Override
	public ReportData updateReport(ReportData report) {
		report = dao.update(report);
		invalidateCache();
		return report;
	}

}
