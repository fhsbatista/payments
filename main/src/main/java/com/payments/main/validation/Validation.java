package com.payments.main.validation;

public interface Validation {
    void validate(Object input) throws ValidationException;
}
