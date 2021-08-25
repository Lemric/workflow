package com.labudzinski.workflow;

import com.labudzinski.workflow.exceptions.LogicException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefinitionTest {

    @Test
    public void testAddPlaces() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Definition definition = new Definition(places);

        assertEquals(5, definition.getPlaces().size());
        assertEquals(places.get("a"), definition.getInitialPlaces().get(0));
    }

    @Test
    public void testSetInitialPlace() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'e'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }
        Definition definition = new Definition(places, new ArrayList<Transition>(), new ArrayList<PlaceInterface>() {{
            add(places.get("c"));
        }}, null);

        assertEquals(places.get("c"), definition.getInitialPlaces().get(0));
    }

    @Test
    public void testSetInitialPlaces() throws Throwable {
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

    @Test
    public void testAddTransition() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'b'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Transition transition = new Transition("name", new ArrayList<PlaceInterface>() {{
            add(places.get("a"));
        }}, new ArrayList<PlaceInterface>() {{
            add(places.get("b"));
        }});
        Definition definition = new Definition(places, new ArrayList<Transition>() {{
            add(transition);
        }}, null, null);

        assertEquals(1, definition.getTransitions().size());
        assertEquals(transition, definition.getTransitions().get(0));
    }

    @Test
    public void testSetInitialPlaceAndPlaceIsNotDefined() {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            new Definition(new HashMap<>(), new ArrayList<>(), new ArrayList<PlaceInterface>() {{
                add(new Place("d"));
            }}, null);
        });
        assertEquals(exception.getMessage(), "Place \"d\" cannot be the initial place as it does not exist.");
    }

    @Test
    public void testAddTransitionAndFromPlaceIsNotDefined() {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            Map<String, PlaceInterface> places = new HashMap<>();
            for (char c = 'a'; c <= 'b'; c++) {
                places.put(String.valueOf(c), new Place(String.valueOf(c)));
            }

            new Definition(places, new ArrayList<>() {{
                add(new Transition("name", new ArrayList<>() {{
                    add(new Place("c"));
                }}, new ArrayList<>() {{
                    add(places.get("b"));
                }}));
            }}, null, null);
        });
        assertEquals(exception.getMessage(), "Place \"c\" referenced in transition \"name\" does not exist.");


    }

    @Test
    public void testAddTransitionAndToPlaceIsNotDefined() {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            Map<String, PlaceInterface> places = new HashMap<>();
            for (char c = 'a'; c <= 'b'; c++) {
                places.put(String.valueOf(c), new Place(String.valueOf(c)));
            }

            new Definition(places, new ArrayList<Transition>() {{
                add(new Transition("name", new ArrayList<PlaceInterface>() {{
                    add(places.get("b"));
                }}, new ArrayList<PlaceInterface>() {{
                    add(new Place("c"));
                }}));
            }}, null, null);
        });
        assertEquals(exception.getMessage(), "Place \"c\" referenced in transition \"name\" does not exist.");
    }

}