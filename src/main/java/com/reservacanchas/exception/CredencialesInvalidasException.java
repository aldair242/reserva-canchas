package com.reservacanchas.exception;

public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Credenciales inválidas");
    }

    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
