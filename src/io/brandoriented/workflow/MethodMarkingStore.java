package io.brandoriented.workflow;

import io.brandoriented.workflow.MarkingStore.MarkingStoreInterface;

public class MethodMarkingStore implements MarkingStoreInterface {
    @Override
    public Marking getMarking(Object subject) {
        return null;
    }

    @Override
    public Marking setMarking(Object subject, Marking marking, String[] context) {
        return null;
    }

    @Override
    public Marking setMarking(Object subject, Marking marking) {
        return null;
    }
}
