package com.labudzinski.workflow;

import java.util.HashMap;
import java.util.Map;

public class TransitionBlocker {
    public static String BLOCKED_BY_MARKING = "19beefc8-6b1e-4716-9d07-a39bd6d16e34";
    public static String BLOCKED_BY_EXPRESSION_GUARD_LISTENER = "326a1e9c-0c12-11e8-ba89-0ed5f89f718b";
    public static String UNKNOWN = "e8b5bbb9-5913-4b98-bfa6-65dbd228a82a";

    private final String message;
    private final String code;
    private final Map<String, Marking> parameters;

    public TransitionBlocker(String message, String code, Map<String, Marking> parameters) {
        this.message = message;
        this.code = code;
        this.parameters = parameters;
    }

    public TransitionBlocker(String message, String code) {
        this.message = message;
        this.code = code;
        this.parameters = null;
    }

    public static TransitionBlocker createBlockedByMarking(Marking marking) {
        return new TransitionBlocker("The marking does not enable the transition.",
                BLOCKED_BY_MARKING, new HashMap<String, Marking>() {{
            put("marking", marking);
        }});
    }

    public static TransitionBlocker createUnknown(String message) {
        return createUnknown(message, 2);
    }

    public static TransitionBlocker createUnknown(String message, int backtraceFrame) {
        if (null != message) {
            return new TransitionBlocker(message, UNKNOWN);
        }

        return new TransitionBlocker("The transition has been blocked by a guard.", UNKNOWN);
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public Map<String, Marking> getParameters() {
        return parameters;
    }
}
