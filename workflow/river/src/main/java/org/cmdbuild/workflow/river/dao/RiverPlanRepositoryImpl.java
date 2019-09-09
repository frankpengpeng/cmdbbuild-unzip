/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.dao;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.workflow.river.dao.repos.PlanData;
import org.cmdbuild.workflow.river.dao.repos.PlanDataRepository;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.model.SimplePlanUpdatedEvent;
import org.cmdbuild.workflow.river.dao.repos.PlanDataImpl;
import static org.cmdbuild.workflow.river.utils.WfRiverXpdlUtils.parseXpdlForCmdb;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.workflow.river.engine.core.PlanImpl;

@Component
public class RiverPlanRepositoryImpl implements ExtendedRiverPlanRepository {

    public final static String ATTR_BIND_TO_CLASS = "cmdbuildBindToClass";

    private final CmCache<Optional<RiverPlan>> riverPlanByPlanId;
    private final CmCache<Optional<RiverPlan>> riverPlanByClassId;

    private final EventBus eventBus = new EventBus();

    private final PlanDataRepository dataRepository;

    public RiverPlanRepositoryImpl(PlanDataRepository dataRepository, CacheService cacheService) {
        this.dataRepository = checkNotNull(dataRepository);
        riverPlanByPlanId = cacheService.newCache("river_plan_by_plan_id", CacheConfig.SYSTEM_OBJECTS);
        riverPlanByClassId = cacheService.newCache("river_plan_by_class_id", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateAll() {
        riverPlanByClassId.invalidateAll();
        riverPlanByPlanId.invalidateAll();
    }

    @Override
    public List<RiverPlanVersionInfo> getPlanVersionsByClassIdOrderByCreationDesc(String classId) {
        return dataRepository.getPlanVersionsByClassIdOrderByCreationDesc(classId);
    }

    @Override
    @Nullable
    public RiverPlan getPlanByIdOrNull(String planId) {
        return riverPlanByPlanId.get(planId, () -> Optional.ofNullable(doGetPlanByIdOrNull(planId))).orElse(null);
    }

    private @Nullable
    RiverPlan doGetPlanByIdOrNull(String planId) {
        return toRiverPlan(dataRepository.getPlanDataByIdOrNull(planId));
    }

    @Override
    public @Nullable
    RiverPlan getPlanByClassIdOrNull(String classeId) {
        return riverPlanByClassId.get(classeId, () -> Optional.ofNullable(doGetPlanByClassIdOrNull(classeId))).orElse(null);
    }

    private @Nullable
    RiverPlan doGetPlanByClassIdOrNull(String classeId) {
        return toRiverPlan(dataRepository.getPlanDataForProcessClasseOrNull(classeId));
    }

    private @Nullable
    RiverPlan toRiverPlan(@Nullable PlanData planData) {
        if (planData == null) {
            return null;
        } else {
            RiverPlan plan = parseXpdlForCmdb(planData.getData());
            plan = PlanImpl.copyOf(plan).withPlanId(planData.getPlanId()).build();
            return plan;
        }
    }

    @Override
    public RiverPlan storePlan(RiverPlan riverPlan) {
        String classeId = riverPlan.getAttr(ATTR_BIND_TO_CLASS),
                planId = riverPlan.getId();
        dataRepository.create(PlanDataImpl.builder()
                .withClasseId(classeId)
                .withPlanId(planId)
                .withData(riverPlan.toXpdl())
                .build());
        invalidateAll();
        eventBus.post(SimplePlanUpdatedEvent.builder()
                .withClassId(classeId)
                .withPlanId(planId)
                .build());
        return riverPlan;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
}
