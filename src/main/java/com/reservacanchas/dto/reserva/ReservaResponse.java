package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.EstadoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponse(
        Long id,
        String codigo,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        EstadoReserva estado,
        BigDecimal precioTotal,
        String motivoCancelacion,
        Long organizadorId,
        String organizadorNombre,
        Long canchaId,
        String canchaNombre,
        boolean pagada
) {
}
