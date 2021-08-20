package com.labudzinski.workflow.metadata;

import com.labudzinski.workflow.PlaceInterface;
import com.labudzinski.workflow.Transition;

import java.util.ArrayList;

public interface MetadataStoreInterface {
    ArrayList getWorkflowMetadata();

    Object getPlaceMetadata(PlaceInterface place);

    Object getTransitionMetadata(Transition transition);

    void getMetadata(String key, Object ssubject);
}
