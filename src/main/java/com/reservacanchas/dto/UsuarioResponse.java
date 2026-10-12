package com.reservacanchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos basicos de un usuario")
public record UsuarioResponse(
        @Schema(description = "Identificador del usuario", example = "1")
        Long id,

        @Schema(description = "Nombre", example = "Juan")
        String nombre,

        @Schema(description = "Apellido", example = "Perez")
        String apellido,

        @Schema(description = "Telefono", example = "912345678")
        String telefono,

        @Schema(description = "Correo electronico", example = "juan@ejemplo.com")
        String correo,

        @Schema(description = "Fecha de registro", example = "2026-10-10T10:00:00")
        LocalDateTime fechaRegistro,

        @Schema(description = "Indica si la cuenta se encuentra activa", example = "true")
        boolean activo
) {
}
