/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow;

import com.labudzinski.eventdispatcher.EventDispatcher;
import com.labudzinski.workflow.event.*;
import com.labudzinski.workflow.exceptions.LogicException;
import com.labudzinski.workflow.exceptions.NotEnabledTransitionException;
import com.labudzinski.workflow.exceptions.UndefinedTransitionException;
import com.labudzinski.workflow.markingstore.MarkingStoreInterface;
import com.labudzinski.workflow.markingstore.MethodMarkingStore;
import com.labudzinski.workflow.metadata.MetadataStoreInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Workflow implements WorkflowInterface {
    public static String DISABLE_LEAVE_EVENT = "workflow_disable_leave_event";
    public static String DISABLE_TRANSITION_EVENT = "workflow_disable_transition_event";
    public static String DISABLE_ENTER_EVENT = "workflow_disable_enter_event";
    public static String DISABLE_ENTERED_EVENT = "workflow_disable_entered_event";
    public static String DISABLE_COMPLETED_EVENT = "workflow_disable_completed_event";
    public static String DISABLE_ANNOUNCE_EVENT = "workflow_disable_announce_event";
    public static Map<String, Boolean> DEFAULT_INITIAL_CONTEXT = new HashMap<>() {{
        put("initial", true);
    }};
    public static Map<String, String> DISABLE_EVENTS_MAPPING = new HashMap<>() {{
        put(WorkflowEvents.LEAVE, DISABLE_LEAVE_EVENT);
        put(WorkflowEvents.ENTER, DISABLE_ENTER_EVENT);
        put(WorkflowEvents.TRANSITION, DISABLE_TRANSITION_EVENT);
        put(WorkflowEvents.ENTERED, DISABLE_ENTERED_EVENT);
        put(WorkflowEvents.COMPLETED, DISABLE_COMPLETED_EVENT);
        put(WorkflowEvents.ANNOUNCE, DISABLE_ANNOUNCE_EVENT);
    }};

    private final Definition definition;
    private MarkingStoreInterface markingStore = null;
    private String name = "unnamed";
    private Map<String, String> eventsToDispatch = null;

    private EventDispatcher dispatcher = new EventDispatcher();

    public Workflow(Definition definition,
                    MarkingStoreInterface markingStore,
                    EventDispatcher dispatcher,
                    String name,
                    Map<String, String> eventsToDispatch) {
        this.definition = definition;
        this.markingStore = markingStore == null ? new MethodMarkingStore() : markingStore;
        this.dispatcher = dispatcher;
        this.name = name;
        this.eventsToDispatch = eventsToDispatch;
    }

    public Workflow(Definition definition, MarkingStoreInterface markingStore) {

        this.definition = definition;
        this.markingStore = markingStore;
    }

    public Workflow(Definition definition, MarkingStoreInterface markingStore, EventDispatcher dispatcher) {

        this.definition = definition;
        this.markingStore = markingStore;
        this.dispatcher = dispatcher;
    }

    public Workflow(Definition definition, MarkingStoreInterface markingStore, EventDispatcher dispatcher, String name) {

        this.definition = definition;
        this.markingStore = markingStore;
        this.dispatcher = dispatcher;
        this.name = name;
    }

    public Marking getMarking(Object subject) throws Exception {
        return this.getMarking(subject, null);
    }

    public Marking getMarking(Object subject, Map<String, Boolean> context) throws Exception {

        Marking marking = this.markingStore.getMarking(subject);
        if (marking == null) {
            throw new LogicException(String.format("The value returned by the MarkingStore is not an instance of \"%s\" for workflow \"%s\".", Marking.class, this.name));
        }
        if (marking.getPlaces().isEmpty()) {
            if (this.definition.getInitialPlaces() == null || this.definition.getInitialPlaces().isEmpty()) {
                throw new LogicException(String.format("The Marking is empty and there is no initial place for workflow \"%s\".", this.name));
            }
            this.definition.getInitialPlaces().forEach((PlaceInterface place) -> {
                marking.mark(place.getName());
            });
            this.markingStore.setMarking(subject, marking);

            if (context == null || context.isEmpty()) {
                context = DEFAULT_INITIAL_CONTEXT;
            }

            this.entered(subject, null, marking, context);
        }
        Map<String, PlaceInterface> places = this.definition.getPlaces();
        for (Map.Entry<String, Integer> stringIntegerEntry : marking.getPlaces().entrySet()) {
            String placeName = stringIntegerEntry.getKey();
            if (!places.containsKey(placeName) || places.get(placeName) == null) {
                String message = String.format("Place \"%s\" is not valid for workflow \"%s\".", placeName, this.name);
                if (places == null) {
                    message += (" It seems you forgot to add places to the current workflow.");
                }

                throw new LogicException(message);
            }
        }

        return marking;
    }

    public boolean can(Object subject, String transitionName) throws Exception {
        ArrayList<Transition> transitions = this.definition.getTransitions();
        Marking marking = this.getMarking(subject);

        for (Transition transition : transitions) {
            if (!transition.getName().equals(transitionName)) {
                continue;
            }

            TransitionBlockerList transitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (transitionBlockerList.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public Marking apply(Object subject, String transitionName) throws Exception {
        return this.apply(subject, transitionName, null);
    }

    public Marking apply(Object subject, String transitionName, Map<String, Boolean> context) throws Exception {
        Marking marking = this.getMarking(subject, context);

        boolean transitionExist = false;
        ArrayList<Transition> approvedTransitions = new ArrayList<>();
        TransitionBlockerList bestTransitionBlockerList = new TransitionBlockerList();

        for (Transition transition : this.definition.getTransitions()) {
            if (!transition.getName().equals(transitionName)) {
                continue;
            }

            transitionExist = true;
            TransitionBlockerList tmpTransitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (tmpTransitionBlockerList.isEmpty()) {
                approvedTransitions.add(transition);
                continue;
            }

            if (!bestTransitionBlockerList.isEmpty()) {
                bestTransitionBlockerList = tmpTransitionBlockerList;
                continue;
            }

            // We prefer to return transitions blocker by something else than
            // marking. Because it means the marking was OK. Transitions are
            // deterministic: it's not possible to have many transitions enabled
            // at the same time that match the same marking with the same name
            if (!tmpTransitionBlockerList.has(TransitionBlocker.BLOCKED_BY_MARKING)) {
                bestTransitionBlockerList = tmpTransitionBlockerList;
            }
        }

        if (!transitionExist) {
            throw new UndefinedTransitionException(subject, transitionName, this, context);
        }

        if (approvedTransitions.isEmpty()) {
            throw new NotEnabledTransitionException(subject, transitionName, this, bestTransitionBlockerList, context);
        }

        for (Transition transition : approvedTransitions) {

            this.leave(subject, transition, marking, context);
            context = this.transition(subject, transition, marking, context);
            this.enter(subject, transition, marking, context);

            this.markingStore.setMarking(subject, marking, context);

            this.entered(subject, transition, marking, context);
            this.completed(subject, transition, marking, context);
            this.announce(subject, transition, marking, context);
        }

        return marking;
    }

    public ArrayList<Transition> getEnabledTransitions(Object subject) throws Exception {
        ArrayList<Transition> enabledTransitions = new ArrayList<>();
        Marking marking = this.getMarking(subject);

        for (Transition transition : this.definition.getTransitions()) {
            TransitionBlockerList transitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (transitionBlockerList.isEmpty()) {
                enabledTransitions.add(transition);
            }
        }

        return enabledTransitions;
    }

    private Transition getEnabledTransition(Object subject, String name) throws Exception {
        Marking marking = this.getMarking(subject);
        for (Transition transition : this.definition.getTransitions()) {
            if (!transition.getName().equals(name)) {
                continue;
            }
            TransitionBlockerList transitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (!transitionBlockerList.isEmpty()) {
                continue;
            }

            return transition;
        }

        return null;
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    public MarkingStoreInterface getMarkingStore() {
        return markingStore;
    }

    @Override
    public String getName() {
        return name;
    }

    public MetadataStoreInterface getMetadataStore() {
        return this.definition.getMetadataStore();
    }

    public TransitionBlockerList buildTransitionBlockerList(Object subject, String transitionName) throws Exception {
        ArrayList<Transition> transitions = this.definition.getTransitions();
        Marking marking = this.getMarking(subject);
        TransitionBlockerList transitionBlockerList = null;
        for (Transition transition : transitions) {
            if (!Objects.equals(transition.getName(), transitionName)) {
                continue;
            }

            transitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (transitionBlockerList.isEmpty()) {
                return transitionBlockerList;
            }

            if (!transitionBlockerList.has(TransitionBlocker.BLOCKED_BY_MARKING)) {
                return transitionBlockerList;
            }
        }


        if (transitionBlockerList == null) {
            throw new UndefinedTransitionException(subject, transitionName, this);
        }

        return transitionBlockerList;
    }

    private GuardEvent guardTransition(Object subject, Marking marking, Transition transition) {
        if (null == this.dispatcher) {
            return null;
        }

        GuardEvent event = new GuardEvent(subject, marking, transition, this);
        this.dispatcher.dispatch(event, WorkflowEvents.GUARD);
        this.dispatcher.dispatch(event, String.format("workflow.%s.guard", this.name));
        this.dispatcher.dispatch(event, String.format("workflow.%s.guard.%s", this.name, transition.getName()));
        return event;
    }

    private void leave(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        ArrayList<PlaceInterface> places = transition.getFroms();

        if (this.shouldDispatchEvent(WorkflowEvents.LEAVE, context)) {
            LeaveEvent event = new LeaveEvent(subject, marking, transition, this, context);

            this.dispatcher.dispatch(event, WorkflowEvents.LEAVE);
            this.dispatcher.dispatch(event, String.format("workflow.%s.leave", this.name));
            for (PlaceInterface place : places) {
                this.dispatcher.dispatch(event, String.format("workflow.%s.leave.%s", this.name, place));
            }
        }
        for (PlaceInterface place : places) {
            marking.unmark(place.getName());
        }
    }

    private Map<String, Boolean> transition(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.TRANSITION, context)) {
            return context;
        }

        Event event = new TransitionEvent(subject, marking, transition, this, context);

        this.dispatcher.dispatch(event, WorkflowEvents.TRANSITION);
        this.dispatcher.dispatch(event, String.format("workflow.%s.transition", this.name));
        this.dispatcher.dispatch(event, String.format("workflow.%s.transition.%s", this.name, transition.getName()));

        return event.getContext();
    }

    private void enter(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        ArrayList<PlaceInterface> places = transition.getTos();

        if (this.shouldDispatchEvent(WorkflowEvents.ENTER, context)) {
            Event event = new EnterEvent(subject, marking, transition, this, context);

            this.dispatcher.dispatch(event, WorkflowEvents.ENTER);

            this.dispatcher.dispatch(event, String.format("workflow.%s.enter", this.name));
            for (PlaceInterface place : places) {
                this.dispatcher.dispatch(event, String.format("workflow.%s.enter.%s", this.name, place));
            }
        }
        for (PlaceInterface place : places) {
            marking.mark(place.getName());
        }
    }

    private void entered(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.ENTERED, context)) {
            return;
        }

        Event event = new EnteredEvent(subject, marking, transition, this, context);
        this.dispatcher.dispatch(event, WorkflowEvents.ENTERED);
        this.dispatcher.dispatch(event, String.format("workflow.%s.enter", this.name));
        for (Map.Entry<String, Integer> place : marking.getPlaces().entrySet()) {
            this.dispatcher.dispatch(event, String.format("workflow.%s.enter.%s", this.name, place.getKey()));
        }
    }

    private void completed(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.COMPLETED, context)) {
            return;
        }

        Event event = new CompletedEvent(subject, marking, transition, this, context);

        this.dispatcher.dispatch(event, WorkflowEvents.COMPLETED);
        this.dispatcher.dispatch(event, String.format("workflow.%s.completed", this.name));
        this.dispatcher.dispatch(event, String.format("workflow.%s.completed.%s", this.name, transition.getName()));
    }

    private void announce(Object subject, Transition initialTransition, Marking marking, Map<String, Boolean> context) throws Exception {
        if (!this.shouldDispatchEvent(WorkflowEvents.ANNOUNCE, context)) {
            return;
        }

        Event event = new AnnounceEvent(subject, marking, initialTransition, this, context);

        this.dispatcher.dispatch(event, WorkflowEvents.ANNOUNCE);
        this.dispatcher.dispatch(event, String.format("workflow.%s.announce", this.name));
        for (Transition transition : this.getEnabledTransitions(subject)) {
            this.dispatcher.dispatch(event, String.format("workflow.%s.announce.%s", this.name, transition.getName()));
        }
    }

    public TransitionBlockerList buildTransitionBlockerListForTransition(Object subject,
                                                                         Marking marking,
                                                                         Transition transition) {
        for (PlaceInterface place : transition.getFroms()) {
            if (!marking.has(place.getName())) {
                return new TransitionBlockerList(new ArrayList<>() {{
                    this.add(TransitionBlocker.createBlockedByMarking(marking));
                }});
            }
        }

        if (null == this.dispatcher) {
            return new TransitionBlockerList();
        }

        GuardEvent event = this.guardTransition(subject, marking, transition);

        if (event != null && event.isBlocked()) {
            return event.getTransitionBlockerList();
        }

        return new TransitionBlockerList();
    }


    private boolean shouldDispatchEvent(String eventName, Map<String, Boolean> context) {
        if (null == this.dispatcher) {
            return false;
        }
        if (context == null) {
            return false;
        }
        if (context.containsKey(DISABLE_EVENTS_MAPPING.get(eventName))) {
            return false;
        }

        if (null == this.eventsToDispatch) {
            return true;
        }

        if (this.eventsToDispatch.isEmpty()) {
            return false;
        }

        return eventsToDispatch.containsKey(eventName);
    }
}
