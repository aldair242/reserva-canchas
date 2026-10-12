package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoPartido;

import java.time.LocalDate;
import java.time.LocalTime;

public record InvitacionResponse(
        Long partidoId,
        EstadoPartido estado,
        String nombreEquipoA,
        String nombreEquipoB,
        int golesEquipoA,
        int golesEquipoB,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        String canchaNombre,
        String sedeNombre
) {
}
