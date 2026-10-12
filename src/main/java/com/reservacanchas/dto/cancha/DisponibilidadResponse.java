package com.reservacanchas.dto.cancha;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Schema(description = "Disponibilidad horaria y bloqueos de una cancha para una fecha dada")
public record DisponibilidadResponse(
        @Schema(description = "Id de la cancha", example = "1")
        Long canchaId,

        @Schema(description = "Fecha consultada", example = "2026-10-15")
        LocalDate fecha,

        @Schema(description = "Hora de apertura de la sede", example = "08:00:00")
        LocalTime horaApertura,

        @Schema(description = "Hora de cierre de la sede", example = "23:00:00")
        LocalTime horaCierre,

        @Schema(description = "Lista de bloqueos o mantenimientos programados en la fecha")
        List<BloqueoInfo> bloqueos,

        @Schema(description = "Turnos libres disponibles para reserva")
        List<TurnoDisponible> turnosDisponibles
) {

    @Schema(description = "Informacion de un bloqueo de cancha")
    public record BloqueoInfo(
            @Schema(description = "Inicio del bloqueo", example = "2026-10-15T08:00:00")
            LocalDateTime inicio,

            @Schema(description = "Fin del bloqueo", example = "2026-10-15T10:00:00")
            LocalDateTime fin,

            @Schema(description = "Motivo del bloqueo", example = "Mantenimiento general")
            String motivo
    ) {
    }

    @Schema(description = "Turno horario disponible para reserva")
    public record TurnoDisponible(
            @Schema(description = "Hora de inicio del turno", example = "10:00:00")
            LocalTime inicio,

            @Schema(description = "Hora de fin del turno", example = "11:00:00")
            LocalTime fin
    ) {
    }
}
