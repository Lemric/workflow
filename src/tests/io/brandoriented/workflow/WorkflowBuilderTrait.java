package io.brandoriented.workflow;

import io.brandoriented.workflow.metadata.InMemoryMetadataStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface WorkflowBuilderTrait {

    default Definition createComplexWorkflowDefinition() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'g'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Transition transitionWithMetadataDumpStyle = new Transition("t3", new ArrayList<PlaceInterface>() {{
            add(places.get("d"));
        }}, new ArrayList<PlaceInterface>() {{
            add(places.get("e"));
        }});
        ArrayList<Transition> transitions = new ArrayList<Transition>() {{
            add(new Transition("t1", new ArrayList<PlaceInterface>() {{
                add(places.get("a"));
            }}, new ArrayList<PlaceInterface>() {{
                add(places.get("b"));
                add(places.get("c"));
            }}));

            add(new Transition("t2", new ArrayList<PlaceInterface>() {{
                add(places.get("b"));
                add(places.get("c"));
            }}, new ArrayList<PlaceInterface>() {{
                add(places.get("d"));
            }}));

            add(transitionWithMetadataDumpStyle);

            add(new Transition("t4", new ArrayList<PlaceInterface>() {{
                add(places.get("d"));
            }}, new ArrayList<PlaceInterface>() {{
                add(places.get("f"));
            }}));
            add(new Transition("t5", new ArrayList<PlaceInterface>() {{
                add(places.get("e"));
            }}, new ArrayList<PlaceInterface>() {{
                add(places.get("g"));
            }}));
            add(new Transition("t6", new ArrayList<PlaceInterface>() {{
                add(places.get("f"));
            }}, new ArrayList<PlaceInterface>() {{
                add(places.get("g"));
            }}));
        }};


        HashMap<Transition, HashMap<String, String>> transitionsMetadata = new HashMap<Transition, HashMap<String, String>>();
        transitionsMetadata.put(transitionWithMetadataDumpStyle, new HashMap<String, String>() {{
            put("label", "My custom transition label 1");
            put("color", "Red");
            put("arrow_color", "Green");
        }});

        InMemoryMetadataStore inMemoryMetadataStore = new InMemoryMetadataStore(
                null,
                null,
                transitionsMetadata
        );

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
