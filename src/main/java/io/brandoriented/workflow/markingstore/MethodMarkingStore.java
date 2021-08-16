package io.brandoriented.workflow.markingstore;

import io.brandoriented.workflow.Marking;
import io.brandoriented.workflow.exceptions.LogicException;
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
        String method = "get" + StringUtils.ucfirst(this.property);
        try {
            subject.getClass().getDeclaredMethod(method);
        } catch (NoSuchMethodException e) {
            String message = String.format(subject.getClass().getName(), method);
            throw new LogicException(message);
        }
        Marking marking;

        try {
            Class classRef = Class.forName(subject.getClass().getName());
            Method caller = classRef.getDeclaredMethod(method);
            marking = (Marking) caller.invoke(subject);

        } catch (NoSuchMethodException | SecurityException |
                IllegalArgumentException | InvocationTargetException |
                ClassNotFoundException | IllegalAccessException ex) {
            throw new LogicException(ex.getMessage());
        }

        if (null == marking) {
            return new Marking();
        }

        if (this.singleState) {
            HashMap<String, Integer> representation = null;
            representation = new HashMap<>() {{
                put(marking.getSingle(), 1);
            }};
            return new Marking(representation);
        }

        return marking;
    }

    public void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException {
        Map<String, Integer> markingTmp = marking.getPlaces();

        if (this.singleState) {
            String mark = markingTmp.keySet().stream().findFirst().get();
        }

        String method = "set" + StringUtils.ucfirst(this.property);
        try {
            Class classRef = Class.forName(subject.getClass().getName());
            for (Method classRefMethod : classRef.getMethods()) {
                if (classRefMethod.getName().equals(method)) {
                    if (context == null) {
                        classRefMethod.invoke(subject, marking);
                        return;
                    }
                    classRefMethod.invoke(subject, Marking.class, context);
                    return;
                }
            }
            throw new LogicException(String.format("The method \"%s::%s()\" does not exist.", subject.getClass().getName(), method));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void setMarking(Object subject, Marking marking) throws LogicException {
        setMarking(subject, marking, null);
    }
}
