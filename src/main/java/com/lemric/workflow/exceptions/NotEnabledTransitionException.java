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

import com.lemric.workflow.TransitionBlockerList;
import com.lemric.workflow.WorkflowInterface;

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
