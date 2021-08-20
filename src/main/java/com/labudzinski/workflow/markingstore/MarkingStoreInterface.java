package com.labudzinski.workflow.markingstore;

import com.labudzinski.workflow.Marking;
import com.labudzinski.workflow.exceptions.LogicException;

import java.util.Map;

public interface MarkingStoreInterface {
    Marking getMarking(Object subject) throws LogicException;

    void setMarking(Object subject, Marking marking, Map<String, Boolean> context) throws LogicException;

    void setMarking(Object subject, Marking marking) throws LogicException;
}
