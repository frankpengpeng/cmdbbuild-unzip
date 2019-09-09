package org.cmdbuild.test.web.utils;

public class UIDefinitions {

    public static String uiDefinitionWorkflowFieldName_ActivityName () {return uiDefinitionWorkflowFieldName_ActivityName;}
    public static String uiDefinitionWorkflowFieldName_CurrentRole () {return uiDefinitionWorkflowFieldName_CurrentRole;}
    public static String uiDefinitionWorkflowFieldName_Requester () {return uiDefinitionWorkflowFieldName_Requester;}

    /**
     * @return mux number of items rendered by extjs when virtualization is enabled. Kept lower to increase safety of tests
     */
    public static int uiDefinitionGridVirtualizationThreshold () {return gridVirtualizationThreshold;}


    private static String uiDefinitionWorkflowFieldName_ActivityName = "Activity Name";
    private static String uiDefinitionWorkflowFieldName_CurrentRole = "Current role";
    private static String uiDefinitionWorkflowFieldName_Requester = "Requester";
    private static int gridVirtualizationThreshold = 100 - 50;
}
