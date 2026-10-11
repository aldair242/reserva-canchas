package com.reservacanchas.model.tarifa;

import com.reservacanchas.model.Cancha;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TarifaPromocional implements EstrategiaTarifa {

    private final EstrategiaTarifa base;
    private final double porcentajeDescuento;
    private final LocalDate vigenteDesde;
    private final LocalDate vigenteHasta;

    public TarifaPromocional(EstrategiaTarifa base, double porcentajeDescuento,
                             LocalDate vigenteDesde, LocalDate vigenteHasta) {
        this.base = base;
        this.porcentajeDescuento = porcentajeDescuento;
        this.vigenteDesde = vigenteDesde;
        this.vigenteHasta = vigenteHasta;
    }

    public EstrategiaTarifa getBase() {
        return base;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public LocalDate getVigenteDesde() {
        return vigenteDesde;
    }

    public LocalDate getVigenteHasta() {
        return vigenteHasta;
    }

    @Override
    public BigDecimal calcular(Cancha cancha, LocalDate fecha, LocalTime inicio, LocalTime fin) {
        BigDecimal precioBase = base.calcular(cancha, fecha, inicio, fin);

        if (vigenteDesde != null && fecha.isBefore(vigenteDesde)) {
            return precioBase;
        }
        if (vigenteHasta != null && fecha.isAfter(vigenteHasta)) {
            return precioBase;
        }

        BigDecimal descuento = precioBase.multiply(BigDecimal.valueOf(porcentajeDescuento))
                .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        return precioBase.subtract(descuento);
    }
}
