package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoPartido;

import java.time.LocalDateTime;
import java.util.List;

public record PartidoResponse(
        Long id,
        EstadoPartido estado,
        String nombreEquipoA,
        String nombreEquipoB,
        int golesEquipoA,
        int golesEquipoB,
        LocalDateTime inicioReal,
        LocalDateTime finReal,
        String tokenInvitacion,
        Long reservaId,
        List<ParticipanteResponse> participantes,
        List<EventoResponse> eventos
) {
}
