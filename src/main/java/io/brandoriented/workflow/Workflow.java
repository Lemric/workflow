package io.brandoriented.workflow;

import com.google.common.eventbus.EventBus;
import io.brandoriented.workflow.event.*;
import io.brandoriented.workflow.exceptions.NotEnabledTransitionException;
import io.brandoriented.workflow.exceptions.UndefinedTransitionException;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;

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

    private EventBus dispatcher = new EventBus();

    public Workflow(Definition definition,
                    MarkingStoreInterface markingStore,
                    EventBus dispatcher,
                    String name,
                    Map<String, String> eventsToDispatch) {
        this.definition = definition;
        this.markingStore = markingStore == null ? new MethodMarkingStore() : markingStore;
        this.dispatcher = dispatcher;
        this.name = name;
        this.eventsToDispatch = eventsToDispatch;
    }

    public Marking getMarking(Object subject) throws Exception {
        return this.getMarking(subject, null);
    }

    public Marking getMarking(Object subject, Map<String, Boolean> context) throws Exception {
        Marking marking = this.markingStore.getMarking(subject);

        if (marking == null) {
            throw new Exception(String.format(this.name));
        }

        if (marking.getPlaces().isEmpty()) {
            if (this.definition.getInitialPlaces().isEmpty()) {
                throw new Exception(String.format(this.name));
            }
            this.definition.getInitialPlaces().forEach((PlaceInterface place) -> {
                marking.mark(place.getName());
            });
            this.markingStore.setMarking(subject, marking);

            if (context.isEmpty()) {
                context = DEFAULT_INITIAL_CONTEXT;
            }

            //this.entered(subject, null, marking, context);
        }
        Map<String, PlaceInterface> places = this.definition.getPlaces();

        try {
            marking.getPlaces().forEach((placeName, nbToken) -> {
                if (!places.containsKey(placeName)) {
                    String message = String.format(placeName, this.name);
                    if (places.isEmpty()) {
                        message += ("It seems you forgot to add places to the current workflow.");
                    }

                    try {
                        throw new Exception(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
        } catch (Exception exception) {
            throw exception;
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

    public Marking apply(Object subject, String transitionName, Map<String, Boolean> context) throws Exception {
        Marking marking = this.getMarking(subject, context);

        boolean transitionExist = false;
        ArrayList<Transition> approvedTransitions = null;
        TransitionBlockerList bestTransitionBlockerList = null;

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

        if (!approvedTransitions.isEmpty()) {
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

        this.dispatcher.post(event);

        return event;
    }

    private void leave(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        String[] places = transition.getFroms();

        if (this.shouldDispatchEvent(WorkflowEvents.LEAVE, context)) {
            Event event = new LeaveEvent(subject, marking, transition, this, context);

            this.dispatcher.post(event);
        }
        for (String place : places) {
            marking.unmark(place);
        }
    }

    private Map<String, Boolean> transition(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.TRANSITION, context)) {
            return context;
        }

        Event event = new TransitionEvent(subject, marking, transition, this, context);

        this.dispatcher.post(event);

        return event.getContext();
    }

    private void enter(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        String[] places = transition.getTos();

        if (this.shouldDispatchEvent(WorkflowEvents.ENTER, context)) {
            Event event = new EnterEvent(subject, marking, transition, this, context);

            this.dispatcher.post(event);
        }
        for (String place : places) {
            marking.unmark(place);
        }
    }

    private void entered(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.ENTERED, context)) {
            return;
        }

        Event event = new EnteredEvent(subject, marking, transition, this, context);
        this.dispatcher.post(event);
    }

    private void completed(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.COMPLETED, context)) {
            return;
        }

        Event event = new CompletedEvent(subject, marking, transition, this, context);

        this.dispatcher.post(event);
    }

    private void announce(Object subject, Transition transition, Marking marking, Map<String, Boolean> context) {
        if (!this.shouldDispatchEvent(WorkflowEvents.ANNOUNCE, context)) {
            return;
        }

        Event event = new AnnounceEvent(subject, marking, transition, this, context);

        this.dispatcher.post(event);
    }

    public TransitionBlockerList buildTransitionBlockerListForTransition(Object subject,
                                                                         Marking marking,
                                                                         Transition transition) {
        for (String place : transition.getFroms()) {
            if (!marking.has(place)) {
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
        if (context.containsKey(DISABLE_EVENTS_MAPPING.containsKey(eventName))) {
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
