package com.payments.transactions.domain;

public class CustomExceptions extends Exception {
    public static class PersistanceError extends CustomExceptions { }
    public static class UnknownBalance extends CustomExceptions { }
    public static class InsufficientFunds extends CustomExceptions { }
}