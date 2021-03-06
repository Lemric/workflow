/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow.exceptions;

import com.lemric.workflow.WorkflowInterface;

import java.util.Map;

public class TransitionException extends LogicException {
    private final Object subject;
    private final String transitionName;
    private final WorkflowInterface workflow;
    private final Map<String, Boolean> context;

    public TransitionException(Object subject,
                               String transitionName,
                               WorkflowInterface workflow,
                               String message,
                               Map<String, Boolean> context) {

        super(message);
        this.subject = subject;
        this.transitionName = transitionName;
        this.workflow = workflow;
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

    public Map<String, Boolean> getContext() {
        return context;
    }
}
