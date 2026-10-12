package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.model.enums.TipoEvento;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un evento registrado durante el partido")
public record EventoResponse(
        @Schema(description = "ID único del evento", example = "1")
        Long id,

        @Schema(description = "Tipo de evento", example = "GOL")
        TipoEvento tipo,

        @Schema(description = "Equipo correspondiente", example = "EQUIPO_A")
        LadoEquipo equipo,

        @Schema(description = "Fecha y hora del evento")
        LocalDateTime instante,

        @Schema(description = "Minuto de juego", example = "42")
        int minuto,

        @Schema(description = "Origen del evento", example = "SENSOR")
        OrigenEvento origen,

        @Schema(description = "Estado de validación del evento", example = "CONFIRMADO")
        EstadoEvento estado,

        @Schema(description = "Descripción narrativa del evento", example = "Gol del equipo EQUIPO_A anotado por Juan Pérez")
        String descripcion,

        @Schema(description = "Indica si suma al tanteador", example = "true")
        boolean afectaMarcador,

        @Schema(description = "ID del jugador que anotó el gol", example = "5")
        Long anotadorId,

        @Schema(description = "Nombre completo del anotador", example = "Juan Pérez")
        String anotadorNombre,

        @Schema(description = "ID del jugador asistente", example = "6")
        Long asistenteId,

        @Schema(description = "Nombre completo del asistente", example = "Carlos Gómez")
        String asistenteNombre,

        @Schema(description = "Motivo de la amonestación o expulsión", example = "Falta táctica")
        String motivo
) {
}
