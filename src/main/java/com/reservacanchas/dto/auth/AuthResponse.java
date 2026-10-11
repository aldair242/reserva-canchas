package com.reservacanchas.dto.auth;

import com.reservacanchas.dto.JugadorResponse;

public record AuthResponse(
        String token,
        String tipo,
        JugadorResponse jugador
) {
}
