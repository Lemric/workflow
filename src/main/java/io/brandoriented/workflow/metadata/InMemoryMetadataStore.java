package io.brandoriented.workflow.metadata;

import io.brandoriented.workflow.PlaceInterface;
import io.brandoriented.workflow.Transition;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryMetadataStore implements MetadataStoreInterface {

    private final HashMap<Transition, HashMap<String, String>> transitionsMetadata;
    private ArrayList<String> workflowMetadata = null;
    private HashMap<String, HashMap<String, PlaceInterface>> placesMetadata = null;

    public InMemoryMetadataStore(ArrayList<String> workflowMetadata,
                                 HashMap<String, HashMap<String, PlaceInterface>> placesMetadata,
                                 HashMap<Transition, HashMap<String, String>> transitionsMetadata) {
        this.workflowMetadata = workflowMetadata;
        this.placesMetadata = placesMetadata;
        this.transitionsMetadata = transitionsMetadata != null ? transitionsMetadata : new HashMap<Transition, HashMap<String, String>>();
    }

    public ArrayList<String> getWorkflowMetadata() {
        return workflowMetadata;
    }

    public HashMap<String, PlaceInterface> getPlaceMetadata(String place) {
        return this.placesMetadata.get(place);
    }

    public HashMap<String, String> getTransitionMetadata(Transition transition) {
        return this.transitionsMetadata.get(transition);
    }
}
