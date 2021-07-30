package io.brandoriented.workflow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Definition {
    private final ArrayList<PlaceInterface> initialPlaces;
    private final MetadataStoreInterface metadataStore;
    private Map<String, PlaceInterface> places;
    private ArrayList<Transition> transitions;

    public Definition(Map<String, PlaceInterface> places,
                      ArrayList<Transition> transitions,
                      ArrayList<PlaceInterface> initialPlaces,
                      MetadataStoreInterface metadataStore) {

        places.forEach(this::addPlace);
        transitions.forEach(this::addTransition);

        this.initialPlaces = initialPlaces;
        this.metadataStore = metadataStore;
    }

    public void addPlace(String planeName, PlaceInterface place) {
        if (this.places.size() == 0) {
            this.initialPlaces.add(place);
        }

        this.places.put(planeName, place);
    }

    public void addTransition(Transition transition) {
        String name = transition.getName();
        try {
            for (String from : transition.getFroms()) {
                if (!this.places.containsKey(from)) {
                    throw new Exception(String.format(from, name));
                }
            }

            for (String to : transition.getTos()) {
                if (!this.places.containsKey(to)) {
                    throw new Exception(String.format(to, name));
                }
            }

            this.transitions.add(transition);
        } catch (Exception ignored) {
        }
    }

    public Map<String, PlaceInterface> getPlaces() {
        return places;
    }

    public ArrayList<Transition> getTransitions() {
        return transitions;
    }

    public ArrayList<PlaceInterface> getInitialPlaces() {
        return initialPlaces;
    }

    public void setInitialPlaces(PlaceInterface[] places) {
        if (places.length == 0) {
            return;
        }

        try {
            for (PlaceInterface place : places) {
                if (!this.places.containsValue(place)) {
                    throw new Exception(String.format(place.getName()));
                }
            }
            this.initialPlaces.addAll(Arrays.asList(places));
        } catch (Exception ignored) {
        }
    }

    public MetadataStoreInterface getMetadataStore() {
        return metadataStore;
    }
}
