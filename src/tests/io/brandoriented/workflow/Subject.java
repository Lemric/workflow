package io.brandoriented.workflow;

import java.util.HashMap;
import java.util.Map;

final public class Subject {
    private Marking marking;
    private Map<String, Boolean> context;

    public Subject(Marking marking) {
        this.marking = marking;
        context = new HashMap<String, Boolean>();
    }

    public Subject() {
        this.marking = null;
        context = new HashMap<String, Boolean>();
    }

    public Marking getMarking() {
        return marking;
    }

    public void setMarking(Marking marking) {
        this.marking = marking;
        this.context = null;
    }

    public void setMarking(Marking marking, Map<String, Boolean> context) {
        this.marking = marking;
        this.context = context;
    }

    public void setMarking() {
        this.marking = null;
        this.context = null;
    }

    public Map<String, Boolean> getContext() {
        return context;
    }
}
