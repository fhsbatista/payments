package com.payments.main.validation;

public class ValidationException extends Exception {
    public static class MissingField extends ValidationException {}
}
