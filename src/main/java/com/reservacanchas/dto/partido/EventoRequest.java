package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.TipoEvento;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para registrar un evento disciplinario o jugada en el partido")
public record EventoRequest(
        @Schema(description = "Tipo de evento", example = "GOL", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull TipoEvento tipo,

        @Schema(description = "Equipo al que se le imputa el evento", example = "EQUIPO_A", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull LadoEquipo equipo,

        @Schema(description = "Momento temporal del evento")
        LocalDateTime instante,

        @Schema(description = "Minuto de juego", example = "35")
        Integer minuto,

        @Schema(description = "ID del participante asignado al evento", example = "10")
        Long jugadorParticipanteId,

        @Schema(description = "Motivo de la tarjeta o evento", example = "Falta reiterada")
        String motivo
) {
}
