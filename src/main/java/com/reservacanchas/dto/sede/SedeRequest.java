package com.reservacanchas.dto.sede;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record SedeRequest(
        @NotBlank String nombre,
        @NotBlank String direccion,
        @NotBlank String distrito,
        Double latitud,
        Double longitud,
        @NotNull LocalTime horaApertura,
        @NotNull LocalTime horaCierre,
        boolean activa,
        Long administradorSedeId
) {
}
