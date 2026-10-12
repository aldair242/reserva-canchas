package com.reservacanchas.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Credenciales de inicio de sesion")
public record LoginRequest(
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo debe ser valido")
        @Schema(description = "Correo electronico registrado", example = "admin@prueba.com", requiredMode = Schema.RequiredMode.REQUIRED)
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Schema(description = "Contrasena de la cuenta", example = "admin123", requiredMode = Schema.RequiredMode.REQUIRED)
        String password
) {
}
