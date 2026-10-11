package com.reservacanchas.dto.cancha;

import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record CanchaResponse(
        Long id,
        String nombre,
        TipoSuperficie superficie,
        boolean techada,
        boolean iluminacion,
        boolean activa,
        Long sedeId,
        String sedeNombre,
        TipoDeporte deporte,
        int capacidadJugadores,
        FormatoFutbol formato,
        Boolean medioCampo,
        Double alturaRed,
        Boolean esDePlaya,
        Boolean paredesDeVidrio,
        TipoTarifa tipoTarifa,
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
