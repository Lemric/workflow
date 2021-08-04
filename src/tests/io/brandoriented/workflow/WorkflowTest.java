package io.brandoriented.workflow;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class WorkflowTest implements WorkflowBuilderTrait {
    @Test
    public void testGetMarkingWithEmptyInitialMarking() throws Throwable {
        Definition definition = createComplexWorkflowDefinition();
        Subject subject = new Subject(null);
        Workflow workflow = new Workflow(definition, new MethodMarkingStore(), null, null, null);
        Marking marking = workflow.getMarking(subject);

        assertInstanceOf(Marking.class, marking);
        assertTrue(marking.has("a"));
        assertEquals(new Marking(new HashMap<String, Integer>() {{
            put("a", 1);
        }}), subject.getMarking());
    }
}