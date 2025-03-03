package com.payments.main.validation;

public class ValidationException extends Exception {
    public static class MissingField extends ValidationException {
        private final String fieldName;

        public MissingField(String fieldName) {
            this.fieldName = fieldName;
        }

        @Override
        public String toString() {
            return fieldName + " is missing";
        }
    }

    public static class InvalidEmail extends ValidationException {
        @Override
        public String toString() {
            return "email is invalid";
        }
    }
}
