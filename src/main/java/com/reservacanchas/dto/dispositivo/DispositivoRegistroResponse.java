package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;

public record DispositivoRegistroResponse(
        Long id,
        String tipo,
        String codigo,
        String claveApi,
        boolean activo,
        Long canchaId,
        UbicacionSensor ubicacion,
        Integer sensibilidad,
        String urlStream,
        String resolucion,
        Integer fps
) {
}

