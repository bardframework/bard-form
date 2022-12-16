package org.bardframework.flow.exception;

import java.util.HashMap;
import java.util.Map;

public class FlowDataValidationException extends RuntimeException {
    private final Map<String, String> fieldErrors = new HashMap<>();

    public FlowDataValidationException() {
        super("invalid field value");
    }

    public FlowDataValidationException addFieldError(String field, String errorMessage) {
        this.fieldErrors.put(field, errorMessage);
        return this;
    }

    public FlowDataValidationException addFieldError(String field) {
        this.fieldErrors.put(field, null);
        return this;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }
}