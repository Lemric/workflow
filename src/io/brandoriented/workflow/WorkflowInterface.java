package io.brandoriented.workflow;

import io.brandoriented.workflow.MarkingStore.MarkingStoreInterface;

public interface WorkflowInterface {

    Marking getMarking(Object subject);

    boolean can(Object subject, String transitionName);

    TransitionBlockerList buildTransitionBlockerList(Object subject, String transitionName);

    Marking apply(Object subject, String transitionName, String[] context);

    Transition getEnabledTransitions(Object subject);

    String getName();

    Definition getDefinition();

    MarkingStoreInterface getMarkingStore();

    MetadataStoreInterface getMetadataStore();
}
