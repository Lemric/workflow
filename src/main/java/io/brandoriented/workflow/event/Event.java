package io.brandoriented.workflow.event;

import io.brandoriented.workflow.Marking;
import io.brandoriented.workflow.Transition;
import io.brandoriented.workflow.WorkflowInterface;

import java.util.Map;

public abstract class Event extends com.labudzinski.EventDispatcher.Event {
    private final Object subject;
    private Marking marking = null;
    private final Transition transition;
    private final WorkflowInterface workflow;
    protected Map<String, Boolean> context;
    protected boolean blocked = false;

    public Event(Object subject, Marking marking, Transition transition, WorkflowInterface workflow, Map<String, Boolean> context) {
        this.context = context;
        this.subject = subject;
        this.marking = marking;
        this.transition = transition;
        this.workflow = workflow;
    }

    public Event(Object subject, Marking marking, Transition transition, WorkflowInterface workflow) {
        this.workflow = workflow;
        this.subject = subject;
        this.marking = marking;
        this.transition = transition;
    }

    public Map<String, Boolean> getContext() {
        return context;
    }

    public Object getSubject() {
        return subject;
    }

    public Marking getMarking() {
        return marking;
    }

    public Transition getTransition() {
        return transition;
    }

    public WorkflowInterface getWorkflow() {
        return workflow;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
