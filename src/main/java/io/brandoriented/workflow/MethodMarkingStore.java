package io.brandoriented.workflow;

import io.brandoriented.workflow.markingstore.MarkingStoreInterface;

import java.util.Map;

public class MethodMarkingStore implements MarkingStoreInterface {
    @Override
    public Marking getMarking(Object subject) {
        return null;
    }

    @Override
    public Marking setMarking(Object subject, Marking marking, Map<String, Boolean> context) {
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
