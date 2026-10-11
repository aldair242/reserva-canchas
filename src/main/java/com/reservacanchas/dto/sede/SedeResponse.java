package com.reservacanchas.dto.sede;

import java.time.LocalTime;

public record SedeResponse(
        Long id,
        String nombre,
        String direccion,
        String distrito,
        Double latitud,
        Double longitud,
        LocalTime horaApertura,
        LocalTime horaCierre,
        boolean activa,
        Long administradorSedeId
) {
}
