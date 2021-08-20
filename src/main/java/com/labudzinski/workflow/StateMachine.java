package com.labudzinski.workflow;

import com.labudzinski.eventdispatcher.EventDispatcher;
import com.labudzinski.workflow.markingstore.MarkingStoreInterface;
import com.labudzinski.workflow.markingstore.MethodMarkingStore;

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
