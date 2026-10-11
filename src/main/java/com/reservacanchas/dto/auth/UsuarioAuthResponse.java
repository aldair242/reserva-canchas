package com.reservacanchas.dto.auth;

public record UsuarioAuthResponse(
        Long id,
        String nombre,
        String apellido,
        String correo,
        String telefono,
        String rol,
        String apodo,
        String fotoUrl,
        String posicionPreferida
) {
}
