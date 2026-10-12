package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.LadoEquipo;

public record ParticipanteResponse(
        Long id,
        LadoEquipo equipo,
        boolean asistio,
        Long personaId,
        String nombreVisible,
        boolean invitado
) {
}
