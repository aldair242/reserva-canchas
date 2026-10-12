package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.TipoPago;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Schema(description = "Solicitud de procesamiento de pago de reserva")
public record PagoRequest(
        @NotNull
        @Schema(description = "Metodo de pago (TARJETA, BILLETERA, TRANSFERENCIA, EFECTIVO)", example = "TARJETA", requiredMode = Schema.RequiredMode.REQUIRED)
        TipoPago tipoPago,

        @NotNull
        @Schema(description = "Monto a pagar", example = "80.00", requiredMode = Schema.RequiredMode.REQUIRED)
        BigDecimal monto,

        @Schema(description = "Referencia o identificador del pago", example = "TX-998877")
        String referencia,

        @Schema(description = "Ultimos 4 digitos de la tarjeta (solo TARJETA)", example = "4242")
        String ultimosCuatro,

        @Schema(description = "Marca de la tarjeta (solo TARJETA)", example = "VISA")
        String marca,

        @Schema(description = "Proveedor de billetera digital (solo BILLETERA)", example = "Yape")
        String proveedor,

        @Schema(description = "Numero de operacion en billetera (solo BILLETERA)", example = "OP-123456")
        String numeroOperacion,

        @Schema(description = "Banco emisor (solo TRANSFERENCIA)", example = "BCP")
        String banco,

        @Schema(description = "Codigo de operacion bancaria (solo TRANSFERENCIA)", example = "TRANS-554433")
        String codigoOperacion,

        @Schema(description = "Nombre del receptor (solo EFECTIVO)", example = "Recepcionista Juan")
        String recibidoPor
) {
}
