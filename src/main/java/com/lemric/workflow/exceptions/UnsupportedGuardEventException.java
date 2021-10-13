package com.lemric.workflow.exceptions;

public class UnsupportedGuardEventException extends RuntimeException implements WorkflowExceptionInterface {
    public UnsupportedGuardEventException(String message) {
        super(message);
    }
}
