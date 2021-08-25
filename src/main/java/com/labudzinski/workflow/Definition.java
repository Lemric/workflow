package com.labudzinski.workflow;

import com.labudzinski.workflow.exceptions.LogicException;
import com.labudzinski.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Definition {
    private ArrayList<PlaceInterface> initialPlaces = null;
    private MetadataStoreInterface metadataStore = null;
    private final Map<String, PlaceInterface> places = new HashMap<>();
    private final ArrayList<Transition> transitions = new ArrayList<>();

    public Definition(Map<String, PlaceInterface> places) throws Throwable {
        this(places, null, null, null);
    }

    public Definition(Map<String, PlaceInterface> places, ArrayList<Transition> transitions) throws Throwable {
        this(places, transitions, null, null);
    }

    public Definition(Map<String, PlaceInterface> places,
                      ArrayList<Transition> transitions,
                      ArrayList<PlaceInterface> initialPlaces,
                      MetadataStoreInterface metadataStore) throws Throwable {

        this.initialPlaces = new ArrayList<>();

        if (places != null) {
            places.forEach(this::addPlace);
        }
        if (transitions != null) {
            for (Transition transition : transitions) {
                addTransition(transition);
            }
        }

        if (initialPlaces != null) {
            setInitialPlaces(initialPlaces);
        }
        this.metadataStore = metadataStore;
    }

    public void addPlace(String planeName, PlaceInterface place) {
        if (this.places.size() == 0) {
            this.initialPlaces.add(place);
        }

        this.places.put(planeName, place);
    }

    public void addTransition(Transition transition) throws Throwable {
        String name = transition.getName();
        for (PlaceInterface from : transition.getFroms()) {
            if (!this.places.containsKey(from.getName())) {
                throw new LogicException(String.format("Place \"%s\" referenced in transition \"%s\" does not exist.", from.getName(), name));
            }
        }

        for (PlaceInterface to : transition.getTos()) {
            if (!this.places.containsKey(to.getName())) {
                throw new LogicException(String.format("Place \"%s\" referenced in transition \"%s\" does not exist.", to.getName(), name));
            }
        }

        this.transitions.add(transition);
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

    public void setInitialPlaces(ArrayList<PlaceInterface> places) throws Throwable {
        if (places == null || places.size() == 0) {
            return;
        }

        for (PlaceInterface place : places) {
            if (!this.places.containsValue(place)) {
                throw new LogicException(String.format("Place \"%s\" cannot be the initial place as it does not exist.", place.getName()));
            }
        }
        this.initialPlaces = places;
    }

    public MetadataStoreInterface getMetadataStore() {
        return metadataStore;
    }
}
