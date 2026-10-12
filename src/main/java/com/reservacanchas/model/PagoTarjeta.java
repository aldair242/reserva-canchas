package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pagos_tarjeta")
@Getter
@Setter
public class PagoTarjeta extends Pago {

    @Column(name = "ultimos_cuatro", length = 4)
    private String ultimosCuatro;

    @Column(length = 30)
    private String marca;

    @Override
    public boolean procesar() {
        // Simulación de procesamiento con pasarela de tarjeta
        if (ultimosCuatro == null || ultimosCuatro.length() != 4) {
            marcarFallido();
            return false;
        }
        confirmar();
        return true;
    }
}
