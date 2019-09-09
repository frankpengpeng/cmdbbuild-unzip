/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.time.ZonedDateTime;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;

public interface ExtendedRiverPlanRepository extends RiverPlanRepository {

	RiverPlan storePlan(RiverPlan riverPlan);

	@Nullable
	RiverPlan getPlanByClassIdOrNull(String name);

	default RiverPlan getPlanByClasseId(String className) {
		return checkNotNull(getPlanByClassIdOrNull(className), "plan not found for classe = %s", className);
	}

	List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId);

	EventBus getEventBus();

	interface RiverPlanVersionInfo {

		ZonedDateTime getLastUpdate();

		String getPlanId();

	}

}
