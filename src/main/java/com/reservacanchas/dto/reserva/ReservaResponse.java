package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.EstadoReserva;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Detalle de una reserva de cancha")
public record ReservaResponse(
        @Schema(description = "Identificador unico de la reserva", example = "10")
        Long id,

        @Schema(description = "Codigo de confirmacion alfanumerico", example = "RES-A1B2C3D4")
        String codigo,

        @Schema(description = "Fecha de la reserva", example = "2026-10-20")
        LocalDate fecha,

        @Schema(description = "Hora de inicio", example = "19:00:00")
        LocalTime horaInicio,

        @Schema(description = "Hora de fin", example = "20:00:00")
        LocalTime horaFin,

        @Schema(description = "Estado de la reserva (PENDIENTE, CONFIRMADA, CANCELADA, COMPLETADA, NO_SHOW)", example = "PENDIENTE")
        EstadoReserva estado,

        @Schema(description = "Monto total calculado segun la tarifa de la cancha", example = "80.00")
        BigDecimal precioTotal,

        @Schema(description = "Motivo de cancelacion en caso de haber sido cancelada", example = "Lluvia torrencial")
        String motivoCancelacion,

        @Schema(description = "Id del jugador organizador", example = "1")
        Long organizadorId,

        @Schema(description = "Nombre completo del jugador organizador", example = "Lionel Messi")
        String organizadorNombre,

        @Schema(description = "Id de la cancha reservada", example = "1")
        Long canchaId,

        @Schema(description = "Nombre de la cancha reservada", example = "Cancha Principal Futbol 7")
        String canchaNombre,

        @Schema(description = "Indica si la reserva ya cuenta con pago exitoso registrado", example = "false")
        boolean pagada
) {
}
