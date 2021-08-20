package com.labudzinski.workflow;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;

class TransitionTest {

    @Test
    public void testConstructor() {
        Transition transition = new Transition("name", new ArrayList<PlaceInterface>() {{
            add(new Place("a"));
        }}, new ArrayList<PlaceInterface>() {{
            add(new Place("b"));
        }});

        assertSame("name", transition.getName());
        assertSame(new Place("a").getName(), transition.getFroms().get(0).getName());
        assertSame(new Place("b").getName(), transition.getTos().get(0).getName());
    }
}