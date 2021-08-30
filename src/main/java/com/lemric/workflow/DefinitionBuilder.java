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

import com.lemric.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.Map;

public class DefinitionBuilder {
    private final Map<String, PlaceInterface> places;
    private final ArrayList<Transition> transitions;
    private ArrayList<PlaceInterface> initialPlaces;
    private MetadataStoreInterface metadataStore;

    public DefinitionBuilder(Map<String, PlaceInterface> places,
                             ArrayList<Transition> transitions) {
        this.places = places;
        this.transitions = transitions;
    }

    public Definition build() throws Throwable {
        return new Definition(this.places, this.transitions, this.initialPlaces, this.metadataStore);
    }

    public DefinitionBuilder clear() {
        this.places.clear();
        this.transitions.clear();
        this.initialPlaces.clear();
        this.metadataStore = null;

        return this;
    }

    public DefinitionBuilder setInitialPlaces(ArrayList<PlaceInterface> initialPlaces) {
        this.initialPlaces = initialPlaces;
        return this;
    }

    public DefinitionBuilder addPlace(PlaceInterface place) {
        if (this.places.size() == 0) {
            this.initialPlaces.add(place);
        }
        this.places.put(place.getName(), place);

        return this;
    }

    public DefinitionBuilder addTransitions(ArrayList<Transition> transitions) {
        this.transitions.addAll(transitions);

        return this;
    }

    public DefinitionBuilder addTransition(Transition transition) {
        this.transitions.add(transition);

        return this;
    }

    public DefinitionBuilder setMetadataStore(MetadataStoreInterface metadataStore) {
        this.metadataStore = metadataStore;

        return this;
    }
}
