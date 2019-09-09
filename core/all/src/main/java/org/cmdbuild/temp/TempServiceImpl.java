/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static com.google.common.base.Preconditions.checkNotNull;
import javax.annotation.Nullable;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.scheduler.JobClusterMode.CM_RUN_ON_SINGLE_NODE;
import static org.cmdbuild.temp.TempServiceUtils.cardIdToTempId;
import static org.cmdbuild.temp.TempServiceUtils.tempIdToCardId;
import org.springframework.stereotype.Component;

@Component
public class TempServiceImpl implements TempService {

	private final DaoService dao;

	public TempServiceImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@ScheduledJob(value = "0 */10 * * * ?", clusterMode = CM_RUN_ON_SINGLE_NODE) //run every 10 minutes
	public void removeExpiredRecords() {
		dao.getJdbcTemplate().update("DELETE FROM \"_Temp\" WHERE now() > \"BeginDate\" + format('%s seconds', \"TimeToLive\")::interval");
	}

	@Override
	public String storeTempData(byte[] data, long timeToLiveSeconds) {
		TempData record = dao.create(TempDataImpl.builder().withData(data).withTimeToLiveSeconds(timeToLiveSeconds).build());
		return cardIdToTempId(record.getId());
	}

	@Override
	@Nullable
	public byte[] getTempDataOrNull(String key) {
		long id = tempIdToCardId(key);
		ResultRow res = dao.getByIdOrNull(TempData.class, id);
		return res == null ? null : res.toModel(TempData.class).getData();
	}

}
