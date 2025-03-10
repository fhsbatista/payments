package com.payments.transactions.domain;

public class CustomExceptions extends Exception {
    public static class PayerNotFound extends CustomExceptions { }
    public static class PayeeNotFound extends CustomExceptions { }
    public static class PersistanceError extends CustomExceptions { }
    public static class UnknownBalance extends CustomExceptions { }
    public static class InsufficientFunds extends CustomExceptions { }
    public static class NotAuthorized extends CustomExceptions { }
}