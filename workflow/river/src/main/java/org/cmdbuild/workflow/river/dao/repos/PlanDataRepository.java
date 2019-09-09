/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao.repos;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;
import org.cmdbuild.workflow.river.dao.ExtendedRiverPlanRepository;

public interface PlanDataRepository {

	default PlanData getPlanDataForProcessClass(String classeId) {
		return checkNotNull(getPlanDataForProcessClasseOrNull(classeId), "plan data not found for classe = %s", classeId);
	}

	@Nullable
	PlanData getPlanDataByIdOrNull(String planId);

	default PlanData getPlanDataById(String planId) {
		return checkNotNull(getPlanDataByIdOrNull(planId), "card not found for planId = %s", planId);
	}

	void create(PlanData plantData);

	@Nullable
	PlanData getPlanDataForProcessClasseOrNull(String classeId);

	List<ExtendedRiverPlanRepository.RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId);

}
