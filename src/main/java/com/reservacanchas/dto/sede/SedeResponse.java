package com.reservacanchas.dto.sede;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalTime;

@Schema(description = "Detalle de una sede deportiva")
public record SedeResponse(
        @Schema(description = "Identificador unico de la sede", example = "1")
        Long id,

        @Schema(description = "Nombre comercial", example = "Sede Miraflores")
        String nombre,

        @Schema(description = "Direccion", example = "Av. Benavides 1234")
        String direccion,

        @Schema(description = "Distrito", example = "Miraflores")
        String distrito,

        @Schema(description = "Latitud", example = "-12.1215")
        Double latitud,

        @Schema(description = "Longitud", example = "-77.0298")
        Double longitud,

        @Schema(description = "Hora de apertura diaria", example = "07:00:00")
        LocalTime horaApertura,

        @Schema(description = "Hora de cierre diaria", example = "23:00:00")
        LocalTime horaCierre,

        @Schema(description = "Indica si la sede esta en funcionamiento", example = "true")
        boolean activa,

        @Schema(description = "Id del AdministradorSede responsable", example = "2")
        Long administradorSedeId
) {
}
