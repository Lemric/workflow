/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow;

import java.util.HashMap;
import java.util.Map;

final public class Subject {
    private Marking marking = new Marking();
    private Map<String, Boolean> context = new HashMap<String, Boolean>();

    public Subject(Marking marking) {
        this.marking = marking;
    }

    public Subject() {
    }

    public Marking getMarking() {
        return this.marking;
    }

    public void setMarking(Marking marking) {
        this.marking = marking;
        this.context = null;
    }

    public void setMarking(Marking marking, Map<String, Boolean> context) {
        this.marking = marking;
        this.context = context;
    }

    public void setMarking() {
        this.marking = null;
        this.context = null;
    }

    public Map<String, Boolean> getContext() {
        return this.context;
    }
}
