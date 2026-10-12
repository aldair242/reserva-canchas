package com.reservacanchas.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " no encontrado con id: " + id);
    }

    public ResourceNotFoundException(String resource, String clave) {
        super(resource + " no encontrado con identificador: " + clave);
    }
}
