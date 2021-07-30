package io.brandoriented.workflow;

import com.google.common.eventbus.EventBus;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;

import java.util.Map;

public class StateMachine extends Workflow {
    public StateMachine(Definition definition,
                        MarkingStoreInterface markingStore,
                        EventBus dispatcher,
                        String name,
                        Map<String, String> eventsToDispatch) {
        super(definition, markingStore == null ? new MethodMarkingStore(true) : markingStore, dispatcher, name, eventsToDispatch);
    }
}
