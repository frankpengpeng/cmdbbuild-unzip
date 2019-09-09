/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import static java.lang.String.format;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import javax.activation.DataSource;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.workflow.river.engine.RiverPlan;
import org.cmdbuild.workflow.river.engine.data.RiverPlanRepository;
import static org.cmdbuild.workflow.river.engine.utils.PlanToDotGraphPlotter.planToDotGraph;
import static org.cmdbuild.workflow.river.engine.utils.PlanToDotGraphPlotter.planToSimplifiedDotGraph;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.workflow.WorkflowCommonConst.RIVER;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.FlowActivity;
import org.springframework.stereotype.Component;
import org.cmdbuild.workflow.WorkflowGraphService;
import static org.cmdbuild.utils.exec.CmProcessUtils.executeProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WorkflowGraphServiceImpl implements WorkflowGraphService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RiverPlanRepository planRepository;

    public WorkflowGraphServiceImpl(RiverPlanRepository planRepository) {
        this.planRepository = checkNotNull(planRepository);
    }

    @Override
    public DataSource getGraphImageForFlow(Flow card) {
        return getGraphImageForFlow(card, false);
    }

    @Override
    public DataSource getSimplifiedGraphImageForFlow(Flow card) {
        return getGraphImageForFlow(card, true);
    }

    private DataSource getGraphImageForFlow(Flow card, boolean simplified) {
        logger.debug("build flow graph for card = {} simplified = {}", card, simplified);
//        checkArgument(equal(card.getType().getProviderOrNull(), RIVER), "flow graph is available only for river flows"); TODO
        Set<String> activeTasks = card.getFlowActivities().stream().map(FlowActivity::getDefinitionId).collect(toSet());
        RiverPlan plan = planRepository.getPlanById(card.getPlanId());
        String dotGraph = simplified ? planToSimplifiedDotGraph(plan, activeTasks) : planToDotGraph(plan, activeTasks);
        logger.trace("build dot graph for flow = {} graph = \n\n{}\n", card, dotGraph);
        File dir = tempDir(),
                dot = new File(dir, "file.dot"),
                png = new File(dir, "file.png");
        writeToFile(dot, dotGraph);
        logger.debug("execute dot process");
        executeProcess("/bin/sh", "-c", format("dot -Tpng %s -o%s", dot.getAbsolutePath(), png.getAbsolutePath()));
        byte[] data = CmIoUtils.toByteArray(png);
        deleteQuietly(dir);
        logger.debug("return graph image");
        return newDataSource(data, "image/png", format("graph_%s_%s.png", card.getType().getName(), card.getId()));

    }

}
