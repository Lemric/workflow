package io.brandoriented.workflow.metadata;

import io.brandoriented.workflow.PlaceInterface;
import io.brandoriented.workflow.Transition;

import java.util.ArrayList;

public interface MetadataStoreInterface {
    ArrayList getWorkflowMetadata();

    Object getPlaceMetadata(PlaceInterface place);

    Object getTransitionMetadata(Transition transition);

    void getMetadata(String key, Object ssubject);
}
