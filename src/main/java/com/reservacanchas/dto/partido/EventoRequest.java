package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventoRequest(
        @NotNull TipoEvento tipo,
        @NotNull LadoEquipo equipo,
        LocalDateTime instante,
        Integer minuto,
        Long jugadorParticipanteId,
        String motivo
) {
}
