package io.brandoriented.workflow.exceptions;

import io.brandoriented.workflow.WorkflowInterface;

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
