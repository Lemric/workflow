package com.labudzinski.workflow;

import com.labudzinski.workflow.metadata.InMemoryMetadataStore;
import com.labudzinski.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WorkflowBuilderTrait {

    public Definition createComplexWorkflowDefinition() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'g'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Transition transitionWithMetadataDumpStyle = new Transition("t3", new ArrayList<>() {{
            add(places.get("d"));
        }}, new ArrayList<>() {{
            add(places.get("e"));
        }});

        ArrayList<Transition> transitions = new ArrayList<Transition>() {{
            add(new Transition("t1", new ArrayList<>() {{
                add(places.get("a"));
            }}, new ArrayList<>() {{
                add(places.get("b"));
                add(places.get("c"));
            }}));
            add(new Transition("t2", new ArrayList<>() {{
                add(places.get("b"));
                add(places.get("c"));
            }}, new ArrayList<>() {{
                add(places.get("d"));
            }}));
            add(transitionWithMetadataDumpStyle);
            add(new Transition("t4", new ArrayList<>() {{
                add(places.get("d"));
            }}, new ArrayList<>() {{
                add(places.get("f"));
            }}));
            add(new Transition("t5", new ArrayList<>() {{
                add(places.get("e"));
            }}, new ArrayList<>() {{
                add(places.get("g"));
            }}));
            add(new Transition("t6", new ArrayList<>() {{
                add(places.get("f"));
            }}, new ArrayList<>() {{
                add(places.get("g"));
            }}));
        }};


        HashMap<Transition, HashMap<String, String>> transitionsMetadata = new HashMap<>() {{
            put(transitionWithMetadataDumpStyle, new HashMap<>() {{
                put("label", "My custom transition label 1");
                put("color", "Red");
                put("arrow_colo'", "'Green'");
            }});
        }};

        MetadataStoreInterface inMemoryMetadataStore = new InMemoryMetadataStore(new ArrayList(), new ArrayList(), transitionsMetadata);

        return new Definition(places, transitions, null, inMemoryMetadataStore);

        // The graph looks like:
        // +---+     +----+     +---+     +----+     +----+     +----+     +----+     +----+     +---+
        // | a | --> | t1 | --> | c | --> | t2 | --> | d  | --> | t4 | --> | f  | --> | t6 | --> | g |
        // +---+     +----+     +---+     +----+     +----+     +----+     +----+     +----+     +---+
        //             |                    ^          |                                           ^
        //             |                    |          |                                           |
        //             v                    |          v                                           |
        //           +----+                 |        +----+     +----+     +----+                  |
        //           | b  | ----------------+        | t3 | --> | e  | --> | t5 | -----------------+
        //           +----+                          +----+     +----+     +----+
    }

}
