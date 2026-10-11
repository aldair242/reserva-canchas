package com.reservacanchas.model;

public interface IAutenticable {

    boolean autenticar(String password);

    void cambiarPassword(String actual, String nueva);
}
