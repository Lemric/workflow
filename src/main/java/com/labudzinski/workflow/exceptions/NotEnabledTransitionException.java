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
