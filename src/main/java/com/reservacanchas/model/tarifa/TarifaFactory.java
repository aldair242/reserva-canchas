package com.reservacanchas.model.tarifa;

import com.reservacanchas.model.enums.TipoTarifa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public final class TarifaFactory {

    private TarifaFactory() {
    }

    public static EstrategiaTarifa crear(
            TipoTarifa tipo,
            BigDecimal precioHora,
            BigDecimal precioNormal,
            BigDecimal precioPico,
            LocalTime inicioPico,
            LocalTime finPico,
            TipoTarifa tipoBase,
            double porcentajeDescuento,
            LocalDate vigenteDesde,
            LocalDate vigenteHasta) {

        if (tipo == null) {
            return new TarifaEstandar(precioHora != null ? precioHora : BigDecimal.ZERO);
        }

        return switch (tipo) {
            case ESTANDAR -> new TarifaEstandar(precioHora != null ? precioHora : BigDecimal.ZERO);
            case HORA_PICO -> new TarifaHoraPico(
                    precioNormal != null ? precioNormal : BigDecimal.ZERO,
                    precioPico != null ? precioPico : BigDecimal.ZERO,
                    inicioPico != null ? inicioPico : LocalTime.MIN,
                    finPico != null ? finPico : LocalTime.MAX);
            case PROMOCIONAL -> {
                EstrategiaTarifa base = crear(
                        tipoBase != null ? tipoBase : TipoTarifa.ESTANDAR,
                        precioHora, precioNormal, precioPico, inicioPico, finPico,
                        null, 0, null, null);
                yield new TarifaPromocional(base, porcentajeDescuento, vigenteDesde, vigenteHasta);
            }
        };
    }
}
