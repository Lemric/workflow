package io.brandoriented.workflow;

import io.brandoriented.workflow.exceptions.LogicException;
import io.brandoriented.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Definition {
    private ArrayList<PlaceInterface> initialPlaces = null;
    private MetadataStoreInterface metadataStore = null;
    private Map<String, PlaceInterface> places = new HashMap<String, PlaceInterface>();
    private ArrayList<Transition> transitions = new ArrayList<Transition>();

    public Definition(Map<String, PlaceInterface> places,
                      ArrayList<Transition> transitions,
                      ArrayList<PlaceInterface> initialPlaces,
                      MetadataStoreInterface metadataStore) throws Throwable {

        this.places = new HashMap<String, PlaceInterface>();
        this.initialPlaces = new ArrayList<PlaceInterface>();
        this.transitions = new ArrayList<Transition>();

        places.forEach(this::addPlace);
        for (Transition transition : transitions) {
            addTransition(transition);
        }

        setInitialPlaces(initialPlaces);
        this.metadataStore = metadataStore;
    }

    public Definition(Map<String, PlaceInterface> places) {
        this.places = new HashMap<String, PlaceInterface>();
        this.initialPlaces = new ArrayList<PlaceInterface>();
        this.transitions = new ArrayList<Transition>();

        places.forEach(this::addPlace);
    }

    public Definition(Map<String, PlaceInterface> places, ArrayList<Transition> transitions) {
        this.places = places;
        this.transitions = transitions;
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
