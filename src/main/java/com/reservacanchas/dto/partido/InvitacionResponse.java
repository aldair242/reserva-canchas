package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoPartido;

import java.time.LocalDate;
import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle público de un partido para resolver invitación")
public record InvitacionResponse(
        @Schema(description = "ID del partido", example = "10")
        Long partidoId,

        @Schema(description = "Estado actual del partido", example = "PROGRAMADO")
        EstadoPartido estado,

        @Schema(description = "Nombre del equipo local/A", example = "Los Galácticos")
        String nombreEquipoA,

        @Schema(description = "Nombre del equipo visitante/B", example = "Furia FC")
        String nombreEquipoB,

        @Schema(description = "Goles del equipo A", example = "0")
        int golesEquipoA,

        @Schema(description = "Goles del equipo B", example = "0")
        int golesEquipoB,

        @Schema(description = "Fecha de la reserva", example = "2026-10-15")
        LocalDate fecha,

        @Schema(description = "Hora de inicio", example = "18:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de finalización", example = "19:00")
        LocalTime horaFin,

        @Schema(description = "Nombre de la cancha", example = "Cancha 1 - Fútbol 7")
        String canchaNombre,

        @Schema(description = "Nombre de la sede", example = "Sede Central Palermo")
        String sedeNombre,

        @Schema(description = "Participantes actualmente inscritos en el partido")
        java.util.List<ParticipanteResponse> participantes
) {
}
