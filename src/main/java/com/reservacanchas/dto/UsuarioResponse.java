package com.reservacanchas.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String apellido,
        String telefono,
        String correo,
        LocalDateTime fechaRegistro,
        boolean activo
) {
}
