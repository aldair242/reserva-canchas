package com.reservacanchas.dto.reserva;

import com.reservacanchas.model.enums.EstadoPago;
import com.reservacanchas.model.enums.TipoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        TipoPago tipoPago,
        BigDecimal monto,
        EstadoPago estado,
        LocalDateTime fechaPago,
        String referencia
) {
}
