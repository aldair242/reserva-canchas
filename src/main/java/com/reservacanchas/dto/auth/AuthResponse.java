package com.reservacanchas.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta de autenticacion exitosa con token JWT")
public record AuthResponse(
        @Schema(description = "Token JWT firmado", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String token,

        @Schema(description = "Tipo de token", example = "Bearer")
        String tipo,

        @Schema(description = "Datos del usuario autenticado")
        UsuarioAuthResponse usuario
) {
}
