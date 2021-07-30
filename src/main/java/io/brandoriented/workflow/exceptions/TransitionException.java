package io.brandoriented.workflow.exceptions;

import io.brandoriented.workflow.WorkflowInterface;

import java.util.Map;

public class TransitionException extends LogicException {
    private final Object subject;
    private final String transitionName;
    private final WorkflowInterface workflow;
    private final String message;
    private final Map<String, Boolean> context;

    public TransitionException(Object subject,
                               String transitionName,
                               WorkflowInterface workflow,
                               String message,
                               Map<String, Boolean> context) {
        this.subject = subject;
        this.transitionName = transitionName;
        this.workflow = workflow;
        this.message = message;
        this.context = context;
    }

    public Object getSubject() {
        return subject;
    }

    public String getTransitionName() {
        return transitionName;
    }

    public WorkflowInterface getWorkflow() {
        return workflow;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Map<String, Boolean> getContext() {
        return context;
    }
}
