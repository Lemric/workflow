package io.brandoriented.workflow;

import com.labudzinski.EventDispatcher.Closure;
import com.labudzinski.EventDispatcher.ClosureRunnable;
import com.labudzinski.EventDispatcher.EventDispatcher;
import io.brandoriented.workflow.event.Event;
import io.brandoriented.workflow.exceptions.LogicException;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;
import io.brandoriented.workflow.markingstore.MethodMarkingStore;
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
            subject.setMarking(new Marking(new HashMap<>() {{
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

        System.out.println(subject.getMarking().hashCode());
        System.out.println(new Marking(new HashMap<>() {{
            put("a", 1);
        }}).hashCode());
        assertThat(new Marking(new HashMap<>() {{
            put("a", 1);
        }})).isEqualTo(subject.getMarking());
    }

    @Test
    public void testGetMarkingWithExistingMarking() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();
        subject.setMarking(new Marking(new HashMap<>() {{
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

        subject.setMarking(new Marking(new HashMap<>() {{
            put("b", 1);
        }}));

        assertFalse(workflow.can(subject, "t1"));
        // In a workflow net, all "from" places should contain a token to enable
        // the transition.
        assertFalse(workflow.can(subject, "t2"));

        subject.setMarking(new Marking(new HashMap<>() {{
            put("b", 1);
            put("c", 1);
        }}));

        assertFalse(workflow.can(subject, "t1"));
        assertTrue(workflow.can(subject, "t2"));

        subject.setMarking(new Marking(new HashMap<>() {{
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
        Event event;
        eventDispatcher.addListener("workflow.workflow_name.guard.t1", new ClosureRunnable(new Closure() {
            public com.labudzinski.EventDispatcher.Event invoke(Event event) {
                return event;
            }
        }));

        Workflow workflow = new Workflow(definition, new MethodMarkingStore(), eventDispatcher, "workflow_name");

        assertFalse(workflow.can(subject, "t1"));
    }

    @Test
    public void testCanDoesNotTriggerGuardEventsForNotEnabledTransitions() throws Throwable {
        Definition definition = this.createComplexWorkflowDefinition();
        Subject subject = new Subject();

        ArrayList<String> dispatchedEvents = new ArrayList<>();
        EventDispatcher eventDispatcher = new EventDispatcher();

        Workflow workflow = new Workflow(definition, new MethodMarkingStore(), eventDispatcher, "workflow_name");
        System.out.println(subject.getMarking().getPlaces().size());
        workflow.apply(subject, "t1");
        System.out.println(subject.getMarking().getPlaces().size());
        workflow.apply(subject, "t2");
        System.out.println(subject.getMarking().getPlaces().size());

        eventDispatcher.addListener("workflow.workflow_name.guard.t3", new ClosureRunnable(new Closure() {
            public com.labudzinski.EventDispatcher.Event invoke(Event event) {
                dispatchedEvents.add("workflow_name.guard.t3");
                return event;
            }
        }));

        eventDispatcher.addListener("workflow.workflow_name.guard.t4", new ClosureRunnable(new Closure() {
            public com.labudzinski.EventDispatcher.Event invoke(Event event) {
                dispatchedEvents.add("workflow_name.guard.t4");
                return event;
            }
        }));

        workflow.can(subject, "t3");

        assertSame(new ArrayList<>() {{
            add("workflow_name.guard.t3");
        }}, dispatchedEvents);
    }
}