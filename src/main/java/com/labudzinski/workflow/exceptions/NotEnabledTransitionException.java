package com.labudzinski.workflow.exceptions;

import com.labudzinski.workflow.TransitionBlockerList;
import com.labudzinski.workflow.WorkflowInterface;

import java.util.Map;

public class NotEnabledTransitionException extends TransitionException {

    private final TransitionBlockerList transitionBlockerList;

    public NotEnabledTransitionException(Object subject, String transitionName, WorkflowInterface workflow, TransitionBlockerList transitionBlockerList, Map<String, Boolean> context) {
        super(subject,
                transitionName,
                workflow,
                String.format(transitionName, workflow.getName()),
                context
        );

        this.transitionBlockerList = transitionBlockerList;
    }

    public TransitionBlockerList getTransitionBlockerList() {
        return transitionBlockerList;
    }
}
