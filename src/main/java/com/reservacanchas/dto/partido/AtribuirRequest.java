package com.reservacanchas.dto.partido;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud de atribucion de autor y asistente para un gol")
public record AtribuirRequest(
        @Schema(description = "Id del participante que marco el gol", example = "10")
        Long anotadorId,

        @Schema(description = "Id del participante que dio la asistencia (opcional)", example = "11")
        Long asistenteId
) {
}
