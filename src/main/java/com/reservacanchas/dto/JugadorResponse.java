package com.reservacanchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Datos publicos del perfil de un jugador")
public record JugadorResponse(
        @Schema(description = "Identificador unico del jugador", example = "1")
        Long id,

        @Schema(description = "Nombre de pila", example = "Carlos")
        String nombre,

        @Schema(description = "Apellido", example = "Sanchez")
        String apellido,

        @Schema(description = "Numero telefonico", example = "987654321")
        String telefono,

        @Schema(description = "Correo electronico", example = "carlos@ejemplo.com")
        String correo,

        @Schema(description = "Apodo o alias deportivo", example = "El Tanque")
        String apodo,

        @Schema(description = "URL de fotografia de perfil", example = "https://cdn.ejemplo.com/fotos/carlos.jpg")
        String fotoUrl,

        @Schema(description = "Posicion de juego preferida", example = "Delantero")
        String posicionPreferida,

        @Schema(description = "Fecha y hora de registro", example = "2026-10-10T12:00:00")
        LocalDateTime fechaRegistro
) {
}
