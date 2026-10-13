package com.reservacanchas.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Detalle de un Administrador de Sede")
public record AdministradorSedeResponse(
        @Schema(description = "ID del usuario", example = "5")
        Long id,

        @Schema(description = "Nombre", example = "Juan")
        String nombre,

        @Schema(description = "Apellido", example = "Pérez")
        String apellido,

        @Schema(description = "Correo electrónico", example = "admin.norte@canchas.com")
        String correo,

        @Schema(description = "Teléfono", example = "987654321")
        String telefono,

        @Schema(description = "Indica si el usuario está activo", example = "true")
        boolean activo,

        @Schema(description = "IDs de las sedes administradas")
        List<Long> sedeIds,

        @Schema(description = "Nombres de las sedes administradas")
        List<String> sedeNombres
) {
}

