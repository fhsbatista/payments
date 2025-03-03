package com.payments.transactions.domain;

public class CustomExceptions extends Exception {
    public static class PersistanceError extends CustomExceptions { }
}