package com.reservacanchas.dto.sede;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

@Schema(description = "Solicitud de creacion o actualizacion de sede deportiva")
public record SedeRequest(
        @NotBlank
        @Schema(description = "Nombre de la sede", example = "Sede Principal Miraflores", requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @NotBlank
        @Schema(description = "Direccion fisica", example = "Av. Benavides 1234", requiredMode = Schema.RequiredMode.REQUIRED)
        String direccion,

        @NotBlank
        @Schema(description = "Distrito o ciudad", example = "Miraflores", requiredMode = Schema.RequiredMode.REQUIRED)
        String distrito,

        @Schema(description = "Coordenada latitud", example = "-12.1215")
        Double latitud,

        @Schema(description = "Coordenada longitud", example = "-77.0298")
        Double longitud,

        @NotNull
        @Schema(description = "Hora de apertura (HH:mm:ss)", example = "07:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaApertura,

        @NotNull
        @Schema(description = "Hora de cierre (HH:mm:ss)", example = "23:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalTime horaCierre,

        @Schema(description = "Estado de operatividad de la sede", example = "true")
        boolean activa,

        @Schema(description = "Id del AdministradorSede asignado", example = "2")
        Long administradorSedeId
) {
}
