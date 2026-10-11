package com.reservacanchas.dto;

import java.time.LocalDateTime;

public record JugadorResponse(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        String correo,
        String apodo,
        String fotoUrl,
        String posicionPreferida,
        LocalDateTime fechaRegistro
) {
}
