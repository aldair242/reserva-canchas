package com.reservacanchas.dto.auth;

public record AuthResponse(
        String token,
        String tipo,
        UsuarioAuthResponse usuario
) {
}
