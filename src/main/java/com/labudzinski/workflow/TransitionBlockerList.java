/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class TransitionBlockerList implements Iterable {
    private final ArrayList<TransitionBlocker> blockers = new ArrayList<>();

    public TransitionBlockerList() {
    }

    public TransitionBlockerList(ArrayList<TransitionBlocker> blockers) {
        blockers.forEach(this::add);
    }

    public void add(TransitionBlocker transitionBlocker) {
        blockers.add(transitionBlocker);
    }

    public boolean has(String code) {
        for (TransitionBlocker blocker : blockers) {
            return Objects.equals(code, blocker.getCode());
        }
        return false;
    }

    public void clear() {
        blockers.clear();
    }

    public boolean isEmpty() {
        return blockers.isEmpty();
    }

    public int count() {
        return blockers.size();
    }

    @Override
    public Iterator<TransitionBlocker> iterator() {
        return blockers.iterator();
    }
}
