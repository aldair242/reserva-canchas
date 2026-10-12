package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.TipoPago;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull TipoPago tipoPago,
        @NotNull BigDecimal monto,
        String referencia,
        String ultimosCuatro,
        String marca,
        String proveedor,
        String numeroOperacion,
        String banco,
        String codigoOperacion,
        String recibidoPor
) {
}
