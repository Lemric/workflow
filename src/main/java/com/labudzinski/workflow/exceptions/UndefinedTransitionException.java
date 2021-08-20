package com.labudzinski.workflow.exceptions;

import com.labudzinski.workflow.WorkflowInterface;

import java.util.Map;

public class UndefinedTransitionException extends TransitionException {
    public UndefinedTransitionException(Object subject, String transitionName, WorkflowInterface workflow, Map<String, Boolean> context) {
        super(subject,
                transitionName,
                workflow,
                String.format(transitionName, workflow.getName()),
                context
        );
    }
}
