/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow.event;

import com.lemric.workflow.Marking;
import com.lemric.workflow.Transition;
import com.lemric.workflow.WorkflowInterface;

import java.util.Map;

public class Event extends com.lemric.eventdispatcher.Event {
    private final Object subject;
    private Marking marking = null;
    private final Transition transition;
    private final WorkflowInterface workflow;
    protected Map<String, Boolean> context;
    protected boolean blocked = false;

    public Event() {
        this.workflow = null;
        this.subject = null;
        this.marking = null;
        this.transition = null;
    }

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
