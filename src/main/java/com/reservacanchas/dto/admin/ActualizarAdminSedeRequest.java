package com.reservacanchas.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud para actualizar un Administrador de Sede existente")
public record ActualizarAdminSedeRequest(
        @NotBlank
        @Schema(description = "Nombre", example = "Juan")
        String nombre,

        @NotBlank
        @Schema(description = "Apellido", example = "Pérez")
        String apellido,

        @NotBlank
        @Email
        @Schema(description = "Correo electrónico de acceso", example = "admin.norte@canchas.com")
        String correo,

        @Schema(description = "Nueva contraseña (opcional, dejar en blanco para no modificar)", example = "nuevaPass123")
        String password,

        @NotBlank
        @Schema(description = "Teléfono de contacto", example = "987654321")
        String telefono,

        @Schema(description = "Estado activo/inactivo del usuario", example = "true")
        Boolean activo
) {
}
