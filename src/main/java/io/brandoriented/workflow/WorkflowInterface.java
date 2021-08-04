package io.brandoriented.workflow;

import io.brandoriented.workflow.markingstore.MarkingStoreInterface;
import io.brandoriented.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.Map;

public interface WorkflowInterface {

    Marking getMarking(Object subject) throws Exception;

    boolean can(Object subject, String transitionName) throws Exception;

    TransitionBlockerList buildTransitionBlockerList(Object subject, String transitionName) throws Exception;

    Marking apply(Object subject, String transitionName, Map<String, Boolean> context) throws Exception;

    ArrayList<Transition> getEnabledTransitions(Object subject) throws Exception;

    String getName();

    Definition getDefinition();

    MarkingStoreInterface getMarkingStore();

    MetadataStoreInterface getMetadataStore();
}
