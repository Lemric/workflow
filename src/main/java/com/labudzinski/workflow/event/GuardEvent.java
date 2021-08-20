package com.labudzinski.workflow.event;

import com.labudzinski.workflow.*;

import java.util.Map;

public class GuardEvent extends Event {
    private final TransitionBlockerList transitionBlockerList = new TransitionBlockerList();

    public GuardEvent(Object subject, Marking marking, Transition transition, WorkflowInterface workflow, Map<String, Boolean> context) {
        super(subject, marking, transition, workflow, context);
    }

    public GuardEvent(Object subject, Marking marking, Transition transition, WorkflowInterface workflow) {
        super(subject, marking, transition, workflow);
    }

    public TransitionBlockerList getTransitionBlockerList() {
        return transitionBlockerList;
    }

    public void addTransitionBlocker(TransitionBlocker transitionBlocker) {
        this.transitionBlockerList.add(transitionBlocker);
    }

    public boolean isBlocked() {
        return !this.transitionBlockerList.isEmpty();
    }

    public void setBlocked(boolean blocked, String message) {
        if (!blocked) {
            this.transitionBlockerList.clear();

            return;
        }
        this.transitionBlockerList.add(TransitionBlocker.createUnknown(message));
    }
}
