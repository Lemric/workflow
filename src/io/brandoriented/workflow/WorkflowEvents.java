package io.brandoriented.workflow;

import java.util.Map;

public class WorkflowEvents {
    public static String GUARD = "workflow.guard";
    public static String LEAVE = "workflow.leave";
    public static String TRANSITION = "workflow.transition";
    public static String ENTER = "workflow.enter";
    public static String ENTERED = "workflow.entered";
    public static String COMPLETED = "workflow.completed";
    public static String ANNOUNCE = "workflow.announce";

    public static Map<String, String> ALIASES;
}
