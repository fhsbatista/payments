package com.payments.main.validation;

public class RequiredFieldValidation implements Validation {
    private final String fieldName;

    public RequiredFieldValidation(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(Object input) throws ValidationException {
        throw new ValidationException.MissingField();
    }
}
