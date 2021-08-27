/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow.event;

import com.labudzinski.workflow.Marking;
import com.labudzinski.workflow.Transition;
import com.labudzinski.workflow.WorkflowInterface;

import java.util.Map;

public class CompletedEvent extends Event {
    public CompletedEvent(Object subject, Marking marking, Transition transition, WorkflowInterface workflow, Map<String, Boolean> context) {
        super(subject, marking, transition, workflow, context);
    }

    public CompletedEvent(Object subject, Marking marking, Transition transition, WorkflowInterface workflow) {
        super(subject, marking, transition, workflow);
    }
}
