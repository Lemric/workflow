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
        Method method = null;
        String methodName = "get" + StringUtils.ucfirst(this.property);
        try {
            method = subject.getClass().getDeclaredMethod(methodName);
        } catch (NoSuchMethodException e) {
            String message = String.format(subject.getClass().getName(), method);
            throw new LogicException(message);
        }
        String marking = null;

        try {
            marking = (String) method.invoke(subject);

        } catch (SecurityException |
                IllegalArgumentException | InvocationTargetException |
                IllegalAccessException ex) {
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
        Method method = null;
        String methodName = "set" + StringUtils.ucfirst(this.property);
        try {
            method = subject.getClass().getDeclaredMethod(methodName, Marking.class, Map.class);
        } catch (NoSuchMethodException e) {
            String message = String.format("The method \"%s::%s()\" does not exist.", subject.getClass().getName(), method);
            throw new LogicException(e.getMessage());
        }
        try {
            method.invoke(subject, marking, context);
        } catch (SecurityException |
                IllegalArgumentException | InvocationTargetException |
                IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setMarking(Object subject, Marking marking) throws LogicException {
        setMarking(subject, marking, null);
    }
}
