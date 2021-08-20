package com.labudzinski.workflow;

import com.labudzinski.eventdispatcher.util.HashCode;

import java.util.HashMap;
import java.util.Map;

public class Marking {
    private final Map<String, Integer> places = new HashMap<String, Integer>();

    public Marking() {
    }

    public Marking(HashMap<String, Integer> representation) {
        if (representation != null) {
            representation.forEach((place, nbToken) -> this.mark(place));
        }
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

    public String getSingle() {
        for (Map.Entry<String, Integer> stringIntegerEntry : this.places.entrySet()) {
            return stringIntegerEntry.getKey();
        }

        return null;
    }

    @Override
    public int hashCode() {
        HashCode h = new HashCode();
        h.addValue(this.getPlaces());
        return h.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Marking that = (Marking) o;

        return that.hashCode() == hashCode();
    }
}
