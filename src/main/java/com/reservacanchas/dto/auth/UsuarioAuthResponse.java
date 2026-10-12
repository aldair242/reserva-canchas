package com.reservacanchas.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Perfil del usuario devuelto tras autenticacion")
public record UsuarioAuthResponse(
        @Schema(description = "Identificador unico", example = "1")
        Long id,

        @Schema(description = "Nombre", example = "Lionel")
        String nombre,

        @Schema(description = "Apellido", example = "Messi")
        String apellido,

        @Schema(description = "Correo electronico", example = "lionel@futbol.com")
        String correo,

        @Schema(description = "Telefono", example = "987654321")
        String telefono,

        @Schema(description = "Rol del usuario (ADMIN, ADMIN_SEDE, JUGADOR)", example = "JUGADOR")
        String rol,

        @Schema(description = "Apodo si es jugador", example = "La Pulga")
        String apodo,

        @Schema(description = "Foto si es jugador", example = "https://cdn.fotos.com/pulga.jpg")
        String fotoUrl,

        @Schema(description = "Posicion preferida si es jugador", example = "Delantero")
        String posicionPreferida
) {
}
