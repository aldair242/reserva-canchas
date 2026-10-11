package com.reservacanchas.dto.cancha;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record DisponibilidadResponse(
        Long canchaId,
        LocalDate fecha,
        LocalTime horaApertura,
        LocalTime horaCierre,
        List<BloqueoInfo> bloqueos,
        List<TurnoDisponible> turnosDisponibles
) {

    public record BloqueoInfo(
            LocalDateTime inicio,
            LocalDateTime fin,
            String motivo
    ) {
    }

    public record TurnoDisponible(
            LocalTime inicio,
            LocalTime fin
    ) {
    }
}
