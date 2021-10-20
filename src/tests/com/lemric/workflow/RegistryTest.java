package com.lemric.workflow;

import com.lemric.eventdispatcher.EventDispatcher;
import com.lemric.eventdispatcher.exceptions.InvalidArgumentException;
import com.lemric.workflow.markingstore.MethodMarkingStore;
import com.lemric.workflow.supportStrategy.InstanceOfSupportStrategy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RegistryTest extends WorkflowBuilderTrait {

    private Registry registry;

    @BeforeEach
    void setUp() throws Throwable {
        this.registry = new Registry();

        Definition definition = this.createSimpleWorkflowDefinition();
        this.registry.addWorkflow(new Workflow(definition, new MethodMarkingStore(), new EventDispatcher(), "workflow1"), new InstanceOfSupportStrategy(Subject1.class));
        this.registry.addWorkflow(new Workflow(definition, new MethodMarkingStore(), new EventDispatcher(), "workflow2"), new InstanceOfSupportStrategy(Subject2.class));
        this.registry.addWorkflow(new Workflow(definition, new MethodMarkingStore(), new EventDispatcher(), "workflow3"), new InstanceOfSupportStrategy(Subject2.class));
    }

    @AfterEach
    void tearDown() {
        this.registry = null;
    }

    @Test
    public void testHasWithMatch() {
        assertTrue(this.registry.has(new Subject1()));
    }

    @Test
    public void testHasWithoutMatch() {
        assertFalse(this.registry.has(new Subject1(), "nope"));
    }

    @Test
    public void testGetWithSuccess() throws InvalidArgumentException {
        WorkflowInterface workflow = this.registry.get(new Subject1());
        assertInstanceOf(Workflow.class, workflow);
        assertSame("workflow1", workflow.getName());

        workflow = this.registry.get(new Subject1(), "workflow1");
        assertInstanceOf(Workflow.class, workflow);
        assertSame("workflow1", workflow.getName());

        workflow = this.registry.get(new Subject2(), "workflow2");
        assertInstanceOf(Workflow.class, workflow);
        assertSame("workflow2", workflow.getName());
    }

    @Test
    public void testGetWithMultipleMatch() throws InvalidArgumentException {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            WorkflowInterface w1 = this.registry.get(new Subject2());
            assertInstanceOf(Workflow.class, w1);
            assertSame("workflow1", w1.getName());
        });
        assertEquals(exception.getMessage(), "Too many workflows (workflow2, workflow3) match this subject (com.lemric.workflow.RegistryTest$Subject2); set a different name on each and use the second (name) argument of this method.");
    }

    @Test
    public void testGetWithNoMatch() {
        InvalidArgumentException exception = Assertions.assertThrows(InvalidArgumentException.class, () -> {
            WorkflowInterface w1 = this.registry.get(new Object());
            assertInstanceOf(Workflow.class, w1);
            assertSame("workflow1", w1.getName());
        });
        assertEquals(exception.getMessage(), "Unable to find a workflow for class \"java.lang.Object\".");
    }

    @Test
    public void testAllWithOneMatchWithSuccess() {
        List<WorkflowInterface> workflows = this.registry.all(new Subject1());
        assertInstanceOf(List.class, workflows);
        assertEquals(1, workflows.size());
        assertInstanceOf(Workflow.class, workflows.get(0));
        assertSame("workflow1", workflows.get(0).getName());
    }

    @Test
    public void testAllWithMultipleMatchWithSuccess() {
        List<WorkflowInterface> workflows = this.registry.all(new Subject2());
        assertInstanceOf(List.class, workflows);
        assertEquals(2, workflows.size());
        assertInstanceOf(Workflow.class, workflows.get(0));
        assertInstanceOf(Workflow.class, workflows.get(1));
        assertSame("workflow2", workflows.get(0).getName());
        assertSame("workflow3", workflows.get(1).getName());
    }

    @Test
    public void testAllWithNoMatch() {
        List<WorkflowInterface> workflows = this.registry.all(new Object());
        assertInstanceOf(List.class, workflows);
        assertEquals(0, workflows.size());
    }

    class Subject1 {
    }

    class Subject2 {
    }
}
