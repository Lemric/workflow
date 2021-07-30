package io.brandoriented.workflow.MarkingStore;

import io.brandoriented.workflow.Marking;

public interface MarkingStoreInterface {
    Marking getMarking(Object subject);

    Marking setMarking(Object subject, Marking marking, String[] context);

    Marking setMarking(Object subject, Marking marking);
}
