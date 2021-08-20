package com.labudzinski.workflow;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MarkingTest {
    @Test
    public void testMarking() {
        Marking marking = new Marking(new HashMap<String, Integer>() {{
            put("a", 1);
        }});

        assertTrue(marking.has("a"));
        assertFalse(marking.has("b"));

        assertEquals(new HashMap<String, Integer>() {{
            put("a", 1);
        }}.keySet(), marking.getPlaces().keySet());

        marking.mark("b");

        assertTrue(marking.has("a"));
        assertTrue(marking.has("b"));

        assertEquals(new HashMap<String, Integer>() {{
            put("a", 1);
            put("b", 1);
        }}.keySet(), marking.getPlaces().keySet());

        marking.unmark("a");

        assertFalse(marking.has("a"));
        assertTrue(marking.has("b"));

        assertEquals(new HashMap<String, Integer>() {{
            put("b", 1);
        }}.keySet(), marking.getPlaces().keySet());

        marking.unmark("b");

        assertFalse(marking.has("a"));
        assertFalse(marking.has("b"));

        assertEquals(new HashMap<String, Integer>().keySet(), marking.getPlaces().keySet());
    }
}