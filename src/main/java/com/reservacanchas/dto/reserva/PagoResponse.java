package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.EstadoPago;
import com.reservacanchas.model.enums.TipoPago;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Comprobante de resultado del pago")
public record PagoResponse(
        @Schema(description = "Id del pago registrado", example = "5")
        Long id,

        @Schema(description = "Tipo de pago empleado", example = "TARJETA")
        TipoPago tipoPago,

        @Schema(description = "Monto pagado", example = "80.00")
        BigDecimal monto,

        @Schema(description = "Estado de la transaccion (PENDIENTE, PAGADO, FALLIDO, REEMBOLSADO)", example = "PAGADO")
        EstadoPago estado,

        @Schema(description = "Fecha y hora en que se confirmo el pago", example = "2026-10-11T20:45:00")
        LocalDateTime fechaPago,

        @Schema(description = "Referencia del pago", example = "TX-998877")
        String referencia
) {
}
