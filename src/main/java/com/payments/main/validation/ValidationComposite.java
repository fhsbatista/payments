package com.payments.main.validation;

import java.util.List;

public class ValidationComposite implements Validation{
    private final List<Validation> validations;

    public ValidationComposite(List<Validation> validations) {
        this.validations = validations;
    }

    @Override
    public void validate(Object input) throws ValidationException {
        throw new ValidationException();
    }
}
