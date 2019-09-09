/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.constants;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class SystemAttributes {

    public final static String ATTR_ID = "Id",
            ATTR_IDCLASS = "IdClass",
            ATTR_IDCLASS1 = "IdClass1",
            ATTR_IDCLASS2 = "IdClass2",
            ATTR_CURRENTID = "CurrentId",
            ATTR_IDDOMAIN = "IdDomain",
            ATTR_IDOBJ1 = "IdObj1",
            ATTR_IDOBJ2 = "IdObj2",
            ATTR_CODE = "Code",
            ATTR_DESCRIPTION = "Description",
            ATTR_NOTES = "Notes",
            ATTR_BEGINDATE = "BeginDate",
            ATTR_ENDDATE = "EndDate",
            ATTR_USER = "User",
            ATTR_STATUS = "Status",
            ATTR_IDTENANT = "IdTenant",
            ATTR_EMAIL = "Email";

    public final static String ATTR_STATUS_A = "A",
            ATTR_STATUS_N = "N",
            ATTR_STATUS_U = "U";

    public final static Set<String> SYSTEM_ATTRIBUTES_NEVER_INSERTED = ImmutableSet.of(ATTR_ID, ATTR_IDCLASS, ATTR_USER, ATTR_BEGINDATE, ATTR_ENDDATE, ATTR_STATUS, ATTR_IDDOMAIN, ATTR_CURRENTID);
    public final static List<String> SIMPLE_CLASS_RESERVED_ATTRIBUTES = ImmutableList.of(ATTR_ID, ATTR_IDCLASS, ATTR_USER, ATTR_BEGINDATE),
            STANDARD_CLASS_RESERVED_ATTRIBUTES = ImmutableList.of(ATTR_ID, ATTR_IDCLASS, ATTR_CURRENTID, ATTR_USER, ATTR_BEGINDATE, ATTR_ENDDATE, ATTR_STATUS),
            DOMAIN_RESERVED_ATTRIBUTES = ImmutableList.of(ATTR_ID, ATTR_IDDOMAIN, ATTR_CURRENTID, ATTR_USER, ATTR_BEGINDATE, ATTR_ENDDATE, ATTR_STATUS, ATTR_IDCLASS1, ATTR_IDOBJ1, ATTR_IDCLASS2, ATTR_IDOBJ2),
            ALL_RESERVED_ATTRIBUTES = STANDARD_CLASS_RESERVED_ATTRIBUTES;
//	public final static Set<String> STANDARD_CLASS_ONLY_RESERVED_ATTRIBUTES = set(STANDARD_CLASS_RESERVED_ATTRIBUTES).without(SIMPLE_CLASS_RESERVED_ATTRIBUTES).immutable();

    //TODO fix query builder, move this attrs back to flow/commons
    public static final String ATTR_FLOW_ID = "ProcessCode";
    public static final String ATTR_FLOW_STATUS = "FlowStatus";
    public static final String ATTR_TASK_INSTANCE_ID = "ActivityInstanceId";
    public static final String ATTR_NEXT_EXECUTOR = "NextExecutor";
    public static final String ATTR_PREV_EXECUTORS = "PrevExecutors";
    public static final String ATTR_PLAN_INFO = "UniqueProcessDefinition";
    public static final String ATTR_TASK_DEFINITION_ID = "ActivityDefinitionId";
    public static final String FLOW_ATTR_DATA = "FlowData";

    public static final List<String> PROCESS_CLASS_RESERVED_ATTRIBUTES = list(STANDARD_CLASS_RESERVED_ATTRIBUTES).with(ATTR_FLOW_ID, ATTR_FLOW_STATUS, ATTR_TASK_INSTANCE_ID, ATTR_NEXT_EXECUTOR, ATTR_PREV_EXECUTORS, ATTR_PLAN_INFO, ATTR_TASK_DEFINITION_ID, FLOW_ATTR_DATA).immutable();

}
