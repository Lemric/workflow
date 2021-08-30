/*
 * This file is part of the com.lemric package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.lemric.workflow;

import com.lemric.workflow.metadata.InMemoryMetadataStore;
import com.lemric.workflow.metadata.MetadataStoreInterface;

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

        ArrayList<Transition> transitions = new ArrayList<>() {{
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
                put("arrow_colo", "Green");
            }});
        }};

        MetadataStoreInterface inMemoryMetadataStore = new InMemoryMetadataStore(new ArrayList(), new HashMap<>(), transitionsMetadata);

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

    public Definition createWorkflowWithSameNameTransition() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'c'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        ArrayList<Transition> transitions = new ArrayList<>() {{
            add(new Transition("a_to_bc", new ArrayList<>() {{
                add(places.get("a"));
            }}, new ArrayList<>() {{
                add(places.get("b"));
                add(places.get("c"));
            }}));
            add(new Transition("b_to_c", new ArrayList<>() {{
                add(places.get("b"));
            }}, new ArrayList<>() {{
                add(places.get("c"));
            }}));
            add(new Transition("to_a", new ArrayList<>() {{
                add(places.get("b"));
            }}, new ArrayList<>() {{
                add(places.get("a"));
            }}));
            add(new Transition("to_a", new ArrayList<>() {{
                add(places.get("c"));
            }}, new ArrayList<>() {{
                add(places.get("a"));
            }}));
        }};

        return new Definition(places, transitions);
    }

    public Definition createSimpleWorkflowDefinition() throws Throwable {
        Map<String, PlaceInterface> places = new HashMap<>();
        for (char c = 'a'; c <= 'c'; c++) {
            places.put(String.valueOf(c), new Place(String.valueOf(c)));
        }

        Transition transitionWithMetadataDumpStyle = new Transition("t1", new ArrayList<>() {{
            add(places.get("a"));
        }}, new ArrayList<>() {{
            add(places.get("b"));
        }});
        Transition transitionWithMetadataArrowColorPink = new Transition("t1", new ArrayList<>() {{
            add(places.get("a"));
        }}, new ArrayList<>() {{
            add(places.get("b"));
        }});

        ArrayList<Transition> transitions = new ArrayList<>() {{
            add(transitionWithMetadataDumpStyle);
            add(transitionWithMetadataArrowColorPink);
        }};

        HashMap<PlaceInterface, HashMap<String, String>> placesMetadata = new HashMap<>() {{
            put(places.get("c"), new HashMap<>() {{
                put("bg_color", "DeepSkyBlue");
                put("description", "My custom place description");
            }});
        }};
        HashMap<Transition, HashMap<String, String>> transitionsMetadata = new HashMap<>() {{
            put(transitionWithMetadataDumpStyle, new HashMap<>() {{
                put("label", "My custom transition label 2");
                put("color", "Grey");
                put("arrow_colo", "Purple");
            }});
            put(transitionWithMetadataArrowColorPink, new HashMap<>() {{
                put("arrow_colo", "Pink");
            }});
        }};

        MetadataStoreInterface inMemoryMetadataStore = new InMemoryMetadataStore(new ArrayList(), placesMetadata, transitionsMetadata);

        return new Definition(places, transitions, null, inMemoryMetadataStore);

        // The graph looks like:
        // +---+     +----+     +---+     +----+     +---+
        // | a | --> | t1 | --> | b | --> | t2 | --> | c |
        // +---+     +----+     +---+     +----+     +---+
    }

}
