package io.brandoriented.workflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class TransitionBlockerList implements Iterable {
    private ArrayList<TransitionBlocker> blockers;

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
