package com.labudzinski.workflow.exceptions;

public class LogicException extends Exception implements ExceptionInterface {

    public LogicException(String message) {
        super(message);
    }

    public LogicException() {
    }
}
