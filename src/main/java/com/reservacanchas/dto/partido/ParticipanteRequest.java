package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.LadoEquipo;
import jakarta.validation.constraints.NotNull;

public record ParticipanteRequest(
        Long jugadorId,
        String nombreInvitado,
        String apellidoInvitado,
        @NotNull LadoEquipo equipo
) {
}
