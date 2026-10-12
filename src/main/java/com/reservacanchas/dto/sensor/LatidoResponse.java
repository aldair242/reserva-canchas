package com.reservacanchas.dto.sensor;

import java.time.LocalDateTime;

public record LatidoResponse(
        String codigo,
        LocalDateTime ultimoLatido,
        boolean enLinea
) {
}

