package com.labudzinski.workflow;

import com.labudzinski.eventdispatcher.EventDispatcher;
import com.labudzinski.eventdispatchercontracts.EventListenerInterface;
import com.labudzinski.workflow.event.GuardEvent;
import com.labudzinski.workflow.exceptions.LogicException;
import com.labudzinski.workflow.markingstore.MarkingStoreInterface;
import com.labudzinski.workflow.markingstore.MethodMarkingStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class WorkflowTest extends WorkflowBuilderTrait {

    @Test
    public void testGetMarkingWithInvalidStoreReturn() throws Exception {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            Object subject = new Object();
            Workflow workflow = new Workflow(new Definition(new HashMap<>(), new ArrayList<>()), new MarkingStoreInterface() {
                @Override
                public Marking getMarking(Object subject) throws LogicException {
                    return null;
                }

                @Override
                public void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException {
                }

                @Override
                public void setMarking(Object subject, Marking marking) throws LogicException {
                }
            });
            workflow.getMarking(subject);
        });
        assertEquals(exception.getMessage(), String.format("The value returned by the MarkingStore is not an instance of \"%s\" for workflow \"unnamed\".", Marking.class));
    }

    @Test
    public void testGetMarkingWithEmptyDefinition() {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            Subject subject = new Subject();
            Workflow workflow = new Workflow(new Definition(new HashMap<>(), new ArrayList<>()), new MethodMarkingStore());
            workflow.getMarking(subject);
        });
        assertEquals(exception.getMessage(), "The Marking is empty and there is no initial place for workflow \"unnamed\".");
    }

    @Test
    public void testGetMarkingWithImpossiblePlace() throws Exception {
        LogicException exception = Assertions.assertThrows(LogicException.class, () -> {
            Subject subject = new Subject();
            Workflow workflow = new Workflow(new Definition(new HashMap<>(), new ArrayList<>()), new MethodMarkingStore());
            subject.setMarking(new Marking(new HashMap<String, Integer>() {{
                put("nope", 1);
            }}));
            workflow.getMarking(subject);
        });
        assertEquals(exception.getMessage(), "Place \"nope\" is not valid for workflow \"unnamed\".");
    }

    @Test
    public void testGetMarkingWithEmptyInitialMarking() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        Workflow workflow = new Workflow(definition, new MethodMarkingStore());

        Marking marking = workflow.getMarking(subject);

        assertInstanceOf(Marking.class, marking);
        assertTrue(marking.has("a"));

        assertThat(new Marking(new HashMap<String, Integer>() {{
            put("a", 1);
        }})).isEqualTo(subject.getMarking());
    }

    @Test
    public void testGetMarkingWithExistingMarking() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        subject.setMarking(new Marking(new HashMap<String, Integer>() {{
            put("b", 1);
            put("c", 1);
        }}));
        Workflow workflow = new Workflow(definition, new MethodMarkingStore());

        Marking marking = workflow.getMarking(subject);

        assertInstanceOf(Marking.class, marking);
        assertTrue(marking.has("b"));
        assertTrue(marking.has("c"));
    }

    @Test
    public void testCanWithUnexistingTransition() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        Workflow workflow = new Workflow(definition, new MethodMarkingStore());

        assertFalse(workflow.can(subject, "foobar"));
    }

    @Test
    public void testCan() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        Workflow workflow = new Workflow(definition, new MethodMarkingStore());

        assertTrue(workflow.can(subject, "t1"));
        assertFalse(workflow.can(subject, "t2"));

        subject.setMarking(new Marking(new HashMap<String, Integer>() {{
            put("b", 1);
        }}));

        assertFalse(workflow.can(subject, "t1"));
        // In a workflow net, all "from" places should contain a token to enable
        // the transition.
        assertFalse(workflow.can(subject, "t2"));

        subject.setMarking(new Marking(new HashMap<String, Integer>() {{
            put("b", 1);
            put("c", 1);
        }}));

        assertFalse(workflow.can(subject, "t1"));
        assertTrue(workflow.can(subject, "t2"));

        subject.setMarking(new Marking(new HashMap<String, Integer>() {{
            put("f", 1);
        }}));

        assertFalse(workflow.can(subject, "t5"));
        assertTrue(workflow.can(subject, "t6"));
    }

    @Test
    public void testCanWithGuard() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        EventDispatcher eventDispatcher = new EventDispatcher();
        EventListenerInterface<GuardEvent> callback = (GuardEvent event) -> {
            event.setBlocked(true, null);
            return event;
        };
        eventDispatcher.addListener("workflow.workflow_name.guard.t1", callback);

        Workflow workflow = new Workflow(definition, new MethodMarkingStore(), eventDispatcher, "workflow_name");
        assertFalse(workflow.can(subject, "t1"));
    }

    @Test
    public void testCanDoesNotTriggerGuardEventsForNotEnabledTransitions() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        ArrayList<String> dispatchedEvents = new ArrayList<>();
        EventDispatcher eventDispatcher = new EventDispatcher();
        EventListenerInterface<GuardEvent> callback = (GuardEvent event) -> {
            dispatchedEvents.add("workflow_name.guard.t3");
            return event;
        };
        EventListenerInterface<GuardEvent> callback2 = (GuardEvent event) -> {
            dispatchedEvents.add("workflow_name.guard.t4");
            return event;
        };
        eventDispatcher.addListener("workflow.workflow_name.guard.t3", callback);
        eventDispatcher.addListener("workflow.workflow_name.guard.t4", callback2);

        Workflow workflow = new Workflow(definition, new MethodMarkingStore(), eventDispatcher, "workflow_name");
        workflow.apply(subject, "t1");
        workflow.apply(subject, "t2");

        workflow.can(subject, "t3");
        assertThat(dispatchedEvents).isEqualTo(new ArrayList<String>() {{
            add("workflow_name.guard.t3");
        }});
    }
}