package io.brandoriented.workflow;

import com.labudzinski.EventDispatcher.EventDispatcher;
import io.brandoriented.workflow.event.*;
import io.brandoriented.workflow.exceptions.LogicException;
import io.brandoriented.workflow.exceptions.NotEnabledTransitionException;
import io.brandoriented.workflow.exceptions.UndefinedTransitionException;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;
import io.brandoriented.workflow.metadata.MetadataStoreInterface;
import io.brandoriented.workflow.markingstore.MethodMarkingStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Workflow implements WorkflowInterface {
    public static String DISABLE_LEAVE_EVENT = "workflow_disable_leave_event";
    public static String DISABLE_TRANSITION_EVENT = "workflow_disable_transition_event";
    public static String DISABLE_ENTER_EVENT = "workflow_disable_enter_event";
    public static String DISABLE_ENTERED_EVENT = "workflow_disable_entered_event";
    public static String DISABLE_COMPLETED_EVENT = "workflow_disable_completed_event";
    public static String DISABLE_ANNOUNCE_EVENT = "workflow_disable_announce_event";
    public static Map<String, Boolean> DEFAULT_INITIAL_CONTEXT = new HashMap<String, Boolean>() {{
        put("initial", true);
    }};
    public static Map<String, String> DISABLE_EVENTS_MAPPING = new HashMap<String, String>() {{
        put(WorkflowEvents.LEAVE, DISABLE_LEAVE_EVENT);
        put(WorkflowEvents.ENTER, DISABLE_ENTER_EVENT);
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
        if (!(marking instanceof Marking)) {
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
            if (transition.getName() != transitionName) {
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

    public Transition getEnabledTransition(Object subject, String name) throws Exception {
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
            if (transition.getName() != transitionName) {
                continue;
            }

            transitionBlockerList = this.buildTransitionBlockerListForTransition(subject, marking, transition);
            if (transitionBlockerList.isEmpty()) {
                return transitionBlockerList;
            }

            if (!transitionBlockerList.has(TransitionBlocker.BLOCKED_BY_MARKING)) {
                return transitionBlockerList;
            }

            if (!transitionBlockerList.isEmpty()) {
                throw new Exception();
            }
        }
        return transitionBlockerList;
    }

    public GuardEvent guardTransition(Object subject, Marking marking, Transition transition) {
        if (null == this.dispatcher) {
            return null;
        }

        GuardEvent event = new GuardEvent(subject, marking, transition, this);

        this.dispatcher.dispatch(event, WorkflowEvents.GUARD);

        return event;
    }

    private void leave(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        ArrayList<PlaceInterface> places = transition.getFroms();

        if (this.shouldDispatchEvent(WorkflowEvents.LEAVE, context)) {
            LeaveEvent event = new LeaveEvent(subject, marking, transition, this, context);

            this.dispatcher.dispatch(event, WorkflowEvents.LEAVE);
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

        return event.getContext();
    }

    private void enter(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        ArrayList<PlaceInterface> places = transition.getTos();

        if (this.shouldDispatchEvent(WorkflowEvents.ENTER, context)) {
            Event event = new EnterEvent(subject, marking, transition, this, context);

            this.dispatcher.dispatch(event, WorkflowEvents.ENTER);
        }
        for (PlaceInterface place : places) {
            marking.unmark(place.getName());
        }
    }

    private void entered(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.ENTERED, context)) {
            return;
        }

        Event event = new EnteredEvent(subject, marking, transition, this, context);
        this.dispatcher.dispatch(event, WorkflowEvents.ENTERED);
    }

    private void completed(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.COMPLETED, context)) {
            return;
        }

        Event event = new CompletedEvent(subject, marking, transition, this, context);

        this.dispatcher.dispatch(event, WorkflowEvents.COMPLETED);
    }

    private void announce(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.ANNOUNCE, context)) {
            return;
        }

        Event event = new AnnounceEvent(subject, marking, transition, this, context);

        this.dispatcher.dispatch(event, WorkflowEvents.ANNOUNCE);
    }

    public TransitionBlockerList buildTransitionBlockerListForTransition(Object subject,
                                                                         Marking marking,
                                                                         Transition transition) {
        for (PlaceInterface place : transition.getFroms()) {
            if (!marking.has(place.getName())) {
                return new TransitionBlockerList(new ArrayList<TransitionBlocker>() {{
                    this.add(TransitionBlocker.createBlockedByMarking(marking));
                }});
            }
        }

        if (null == this.dispatcher) {
            return new TransitionBlockerList();
        }

        GuardEvent event = this.guardTransition(subject, marking, transition);

        if (event.isBlocked()) {
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
