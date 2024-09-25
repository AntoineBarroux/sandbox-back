package com.sandbox.interactive.employee.exception;

public class SupervisorNotFoundException extends RuntimeException {

    public SupervisorNotFoundException(String message) {
        super(message);
    }
}
