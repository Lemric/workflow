package io.brandoriented.workflow;

import com.labudzinski.EventDispatcher.EventDispatcher;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;
import io.brandoriented.workflow.markingstore.MethodMarkingStore;

import java.util.Map;

public class StateMachine extends Workflow {
    public StateMachine(Definition definition,
                        MarkingStoreInterface markingStore,
                        EventDispatcher dispatcher,
                        String name,
                        Map<String, String> eventsToDispatch) {
        super(definition, markingStore == null ? new MethodMarkingStore(true) : markingStore, dispatcher, name, eventsToDispatch);
    }
}
