package com.labudzinski.workflow;

import com.labudzinski.workflow.event.*;

import java.util.HashMap;
import java.util.Map;

public class WorkflowEvents {
    public static String GUARD = "workflow.guard";
    public static String LEAVE = "workflow.leave";
    public static String TRANSITION = "workflow.transition";
    public static String ENTER = "workflow.enter";
    public static String ENTERED = "workflow.entered";
    public static String COMPLETED = "workflow.completed";
    public static String ANNOUNCE = "workflow.announce";

    public static Map<String, String> ALIASES = new HashMap<String, String>() {{
        put(String.valueOf(GuardEvent.class), GUARD);
        put(String.valueOf(LeaveEvent.class), LEAVE);
        put(String.valueOf(TransitionEvent.class), TRANSITION);
        put(String.valueOf(EnterEvent.class), ENTER);
        put(String.valueOf(EnteredEvent.class), ENTERED);
        put(String.valueOf(CompletedEvent.class), COMPLETED);
        put(String.valueOf(AnnounceEvent.class), ANNOUNCE);
    }};

}
