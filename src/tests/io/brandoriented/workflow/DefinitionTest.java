package io.brandoriented.workflow;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefinitionTest {

    @Test
    public void testAddPlaces() {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Definition definition = new Definition(places);

        assertEquals(5, definition.getPlaces().size());
        assertEquals(places.get("a"), definition.getInitialPlaces().get(0));
    }

    @Test
    public void testSetInitialPlace() {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }
        Definition definition = new Definition(places, new ArrayList<>(), new ArrayList<PlaceInterface>() {{
            add(places.get("c"));
        }}, null);

        assertEquals(places.get("c"), definition.getInitialPlaces().get(0));
    }

    @Test
    public void testSetInitialPlaces() {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Definition definition = new Definition(places, new ArrayList<>(), new ArrayList<PlaceInterface>() {{
            add(places.get("c"));
            add(places.get("d"));
        }}, null);

        assertEquals(places.get("c"), definition.getInitialPlaces().get(0));
        assertEquals(places.get("d"), definition.getInitialPlaces().get(1));
    }

}