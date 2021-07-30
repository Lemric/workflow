package io.brandoriented.workflow;

import io.brandoriented.workflow.exceptions.LogicException;
import io.brandoriented.workflow.markingstore.MarkingStoreInterface;
import io.brandoriented.workflow.tools.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

final public class MethodMarkingStore implements MarkingStoreInterface {
    private boolean singleState = false;
    private String property = "marking";

    @Override
    public Marking setMarking(Object subject, Marking marking, Map<String, Boolean> context) {
        return null;
    }

    @Override
    public Marking setMarking(Object subject, Marking marking, String[] context) {
        return null;
    }

    @Override
    public Marking setMarking(Object subject, Marking marking) {
        return null;
    }

    public MethodMarkingStore(boolean singleState, String property) {
        this.singleState = singleState;
        this.property = property;
    }

    public MethodMarkingStore(boolean singleState) {
        this.singleState = singleState;
    }

    public MethodMarkingStore() {
    }

    @Override
    public Marking getMarking(Object subject) throws LogicException {
        String method = "get" + StringUtils.ucfirst(this.property);
        try {
            subject.getClass().getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            String message = String.format(subject.getClass().getName(), method);
            throw new LogicException(message);
        }
        String marking = null;

        try {
            Class classRef = Class.forName(subject.getClass().getName());
            Method caller = classRef.getDeclaredMethod(method);
            marking = (String) caller.invoke(subject);

        } catch (NoSuchMethodException | SecurityException |
                IllegalArgumentException | InvocationTargetException |
                ClassNotFoundException | IllegalAccessException ex) {
            ex.printStackTrace();
        }

        if (null == marking) {
            return new Marking();
        }

        HashMap<String, Integer> representation = null;
        if (this.singleState) {
            String finalMarking = marking;
            representation = new HashMap<String, Integer>() {{
                put(finalMarking, 1);
            }};
        }

        return new Marking(representation);
    }

    public void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException {
        Map<String, Integer> markingTmp = marking.getPlaces();

        if (this.singleState) {
            String mark = markingTmp.keySet().stream().findFirst().get();
        }

        String method = "set" + StringUtils.ucfirst(this.property);
        try {
            subject.getClass().getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            String message = String.format(subject.getClass().getName(), method);
            throw new LogicException(message);
        }
        try {
            Class classRef = Class.forName(subject.getClass().getName());
            Method caller = classRef.getDeclaredMethod(method);
            caller.invoke(subject, context);

        } catch (NoSuchMethodException | SecurityException |
                IllegalArgumentException | InvocationTargetException |
                ClassNotFoundException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }
}
