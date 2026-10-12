package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoPartido;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle completo de un partido, sus participantes y eventos")
public record PartidoResponse(
        @Schema(description = "ID único del partido", example = "1")
        Long id,

        @Schema(description = "Estado actual del partido", example = "EN_JUEGO")
        EstadoPartido estado,

        @Schema(description = "Nombre del equipo local/A", example = "Los Galácticos")
        String nombreEquipoA,

        @Schema(description = "Nombre del equipo visitante/B", example = "Furia FC")
        String nombreEquipoB,

        @Schema(description = "Goles acumulados del equipo A", example = "2")
        int golesEquipoA,

        @Schema(description = "Goles acumulados del equipo B", example = "1")
        int golesEquipoB,

        @Schema(description = "Fecha y hora de inicio real")
        LocalDateTime inicioReal,

        @Schema(description = "Fecha y hora de finalización real")
        LocalDateTime finReal,

        @Schema(description = "Token UUID para compartir e invitar al partido", example = "43c5b596-3fa7-4fbc-8cf0-23a73c15ca59")
        String tokenInvitacion,

        @Schema(description = "ID de la reserva asociada", example = "1")
        Long reservaId,

        @Schema(description = "Lista de participantes en el partido")
        List<ParticipanteResponse> participantes,

        @Schema(description = "Lista de eventos ocurridos en el partido")
        List<EventoResponse> eventos
) {
}
