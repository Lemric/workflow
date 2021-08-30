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

import com.lemric.workflow.*;

import java.util.Map;

public class GuardEvent<T> extends Event {
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

    public void setBlocked(boolean blocked) {
        setBlocked(blocked, null);
    }

    public void setBlocked(boolean blocked, String message) {
        if (!blocked) {
            this.transitionBlockerList.clear();

            return;
        }
        this.transitionBlockerList.add(TransitionBlocker.createUnknown(message));
    }
}
