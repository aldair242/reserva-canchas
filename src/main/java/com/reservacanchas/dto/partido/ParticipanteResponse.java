package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.LadoEquipo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detalle de un participante en el partido")
public record ParticipanteResponse(
        @Schema(description = "ID del participante en el partido", example = "1")
        Long id,

        @Schema(description = "Equipo asignado", example = "EQUIPO_A")
        LadoEquipo equipo,

        @Schema(description = "Indica si asistió efectivamente al partido", example = "true")
        boolean asistio,

        @Schema(description = "ID de la persona asociada (si es usuario registrado)", example = "3")
        Long personaId,

        @Schema(description = "Nombre visible del participante", example = "Carlos Tévez")
        String nombreVisible,

        @Schema(description = "Indica si es un invitado sin cuenta de usuario", example = "false")
        boolean invitado
) {
}
