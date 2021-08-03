package io.brandoriented.workflow;

import java.util.HashMap;
import java.util.Map;

public class Marking {
    private Map<String, Integer> places;

    public Marking() {
        this.places = new HashMap<>();
    }

    public Marking(HashMap<String, Integer> representation) {
        this.places = new HashMap<>();
        representation.forEach((place, nbToken) -> this.mark(place));
    }

    public void mark(String place) {
        this.places.put(place, 1);
    }

    public void unmark(String place) {
        this.places.remove(place);
    }

    public boolean has(String place) {
        return this.places.containsKey(place);
    }

    public Map<String, Integer> getPlaces() {
        return this.places;
    }
}
