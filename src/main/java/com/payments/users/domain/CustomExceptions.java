package com.payments.users.domain;

public class CustomExceptions extends Exception {
    public static class PersistanceError extends CustomExceptions { }
    public static class EmailAlreadyRegistered extends CustomExceptions { }
    public static class CpfAlreadyRegistered extends CustomExceptions { }
}