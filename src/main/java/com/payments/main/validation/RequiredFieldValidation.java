package com.payments.main.validation;

import java.lang.reflect.Field;

public class RequiredFieldValidation implements Validation {
    private final String fieldName;

    public RequiredFieldValidation(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public void validate(Object input) throws ValidationException {
        try {
            final Field field = input.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(input);

            if (value == null) {
                throw new ValidationException.MissingField();
            }
        } catch (NoSuchFieldException e) {
            throw new ValidationException.MissingField();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
