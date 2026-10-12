package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;

import java.time.LocalDateTime;

public record DispositivoResponse(
        Long id,
        String tipo,
        String codigo,
        LocalDateTime ultimoLatido,
        boolean activo,
        boolean enLinea,
        Long canchaId,
        String canchaNombre,
        UbicacionSensor ubicacion,
        Integer sensibilidad,
        String urlStream,
        String resolucion,
        Integer fps
) {
}

