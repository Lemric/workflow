/*
 * This file is part of the com.labudzinski package.
 * Copyright (c) 2021-2021.
 *
 * For the full copyright and license information, please view the LICENSE file that was distributed with this source code.
 *
 * @author Dominik Labudzinski <dominik@labudzinski.com>
 *
 */

package com.labudzinski.workflow.markingstore;

import com.labudzinski.workflow.Marking;
import com.labudzinski.workflow.exceptions.LogicException;
import com.labudzinski.workflow.tools.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

    public static Object[] getParametersArray(Parameter[] param) {
        return new Object[param.length];
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
        Marking marking;

        try {
            marking = (Marking) method.invoke(subject);

        } catch (SecurityException |
                IllegalArgumentException | InvocationTargetException |
                IllegalAccessException ex) {
            throw new LogicException(ex.getMessage());
        }

        if (null == marking) {
            return new Marking();
        }

        if (this.singleState) {
            HashMap<String, Integer> representation = null;
            representation = new HashMap<String, Integer>() {{
                put(marking.getSingle(), 1);
            }};
            return new Marking(representation);
        }

        return marking;
    }

    @Override
    public void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException {
        Map<String, Integer> markingTmp = marking.getPlaces();

        marking = new Marking((HashMap<String, Integer>) markingTmp);
        String method = "set" + StringUtils.ucfirst(this.property);
        try {
            Class classRef = Class.forName(subject.getClass().getName());
            for (Method classRefMethod : classRef.getMethods()) {
                if (classRefMethod.getName().equals(method)) {
                    try {
                        if (classRefMethod.getParameterCount() == 2) {
                            classRefMethod.invoke(subject, marking, context);
                        } else {
                            classRefMethod.invoke(subject, marking);
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
