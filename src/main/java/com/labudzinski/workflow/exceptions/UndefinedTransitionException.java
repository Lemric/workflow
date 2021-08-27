/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow.exceptions;

import com.labudzinski.workflow.WorkflowInterface;

import java.util.Map;

public class UndefinedTransitionException extends TransitionException {
    public UndefinedTransitionException(Object subject, String transitionName, WorkflowInterface workflow) {
        this(subject, transitionName, workflow, null);
    }

    public UndefinedTransitionException(Object subject, String transitionName, WorkflowInterface workflow, Map<String, Boolean> context) {
        super(subject,
                transitionName,
                workflow,
                String.format("Transition \"%s\" is not defined for workflow \"%s\".", transitionName, workflow.getName()),
                context
        );
    }
}
