package io.brandoriented.workflow;

import io.brandoriented.workflow.MarkingStore.MarkingStoreInterface;
import sun.rmi.server.Dispatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Workflow {
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
    private Dispatcher dispatcher = null;
    private String name = "unnamed";
    private String[] eventsToDispatch = null;

    public Workflow(Definition definition,
                    MarkingStoreInterface markingStore,
                    Dispatcher dispatcher,
                    String name,
                    String[] eventsToDispatch) {
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

        event = this.guardTransition(subject, marking, transition);

        if (event.isBlocked()) {
            return event.getTransitionBlockerList();
        }

        return new TransitionBlockerList();
    }

    public void entered() {

    }

}
