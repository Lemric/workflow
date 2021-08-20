package com.labudzinski.workflow.metadata;

import com.labudzinski.workflow.PlaceInterface;
import com.labudzinski.workflow.Transition;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryMetadataStore implements MetadataStoreInterface {

    private final ArrayList workflowMetadata;
    private final ArrayList placesMetadata;
    private final HashMap<Transition, HashMap<String, String>> transitionsMetadata;

    public InMemoryMetadataStore(ArrayList workflowMetadata,
                                 ArrayList placesMetadata,
                                 HashMap<Transition, HashMap<String, String>> transitionsMetadata) {
        this.workflowMetadata = workflowMetadata;
        this.placesMetadata = placesMetadata;
        this.transitionsMetadata = transitionsMetadata;
    }

    @Override
    public ArrayList getWorkflowMetadata() {
        return this.workflowMetadata;
    }

    @Override
    public Object getPlaceMetadata(PlaceInterface place) {
        return this.placesMetadata.get(this.placesMetadata.indexOf(place));
    }

    @Override
    public HashMap<String, String> getTransitionMetadata(Transition transition) {
        return this.transitionsMetadata.get(transition);
    }

    @Override
    public void getMetadata(String key, Object subject) {

    }
}
