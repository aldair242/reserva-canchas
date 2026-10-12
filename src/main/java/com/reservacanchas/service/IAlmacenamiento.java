package com.reservacanchas.service;

import java.io.File;

public interface IAlmacenamiento {

    String subir(File archivo);

    String generarUrlFirmada(String ruta);

    void eliminar(String ruta);

    default boolean validarFirma(String ruta, Long expires, String signature) {
        return true;
    }
}

