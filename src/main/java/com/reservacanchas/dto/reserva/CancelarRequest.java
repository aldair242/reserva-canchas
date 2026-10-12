package com.reservacanchas.dto.reserva;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Solicitud de cancelacion de reserva")
public record CancelarRequest(
        @NotBlank
        @Schema(description = "Motivo por el cual se cancela la reserva", example = "Imprevisto laboral de los jugadores", requiredMode = Schema.RequiredMode.REQUIRED)
        String motivo
) {
}
