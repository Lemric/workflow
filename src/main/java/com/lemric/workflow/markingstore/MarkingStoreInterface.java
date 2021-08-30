/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow.markingstore;

import com.lemric.workflow.Marking;
import com.lemric.workflow.exceptions.LogicException;

import java.util.Map;

public interface MarkingStoreInterface {
    Marking getMarking(Object subject) throws LogicException;

    void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException;

    void setMarking(Object subject, Marking marking) throws LogicException;
}
