package com.reservacanchas.dto.cancha;

import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CanchaRequest(
        @NotBlank String nombre,
        @NotNull TipoSuperficie superficie,
        boolean techada,
        boolean iluminacion,
        boolean activa,
        @NotNull Long sedeId,
        @NotNull TipoDeporte tipoDeporte,
        FormatoFutbol formato,
        Boolean medioCampo,
        Double alturaRed,
        Boolean esDePlaya,
        Boolean paredesDeVidrio,
        @NotNull TipoTarifa tipoTarifa,
        BigDecimal precioHora,
        BigDecimal precioNormal,
        BigDecimal precioPico,
        LocalTime inicioPico,
        LocalTime finPico,
        TipoTarifa tipoTarifaBase,
        double porcentajeDescuento,
        LocalDate vigenteDesde,
        LocalDate vigenteHasta
) {
}
