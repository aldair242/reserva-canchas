package com.reservacanchas.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Solicitud para registrar un nuevo Administrador de Sede")
public record CrearAdminSedeRequest(
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

        @NotBlank
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        @Schema(description = "Contraseña de acceso", example = "admin123")
        String password,

        @NotBlank
        @Schema(description = "Teléfono de contacto", example = "987654321")
        String telefono
) {
}

