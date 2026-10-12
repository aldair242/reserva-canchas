package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pagos_efectivo")
@Getter
@Setter
public class PagoEfectivo extends Pago {

    @Column(name = "recibido_por", length = 100)
    private String recibidoPor;

    @Override
    public boolean procesar() {
        // Simulación de registro de pago en efectivo
        if (recibidoPor == null || recibidoPor.isBlank()) {
            marcarFallido();
            return false;
        }
        confirmar();
        return true;
    }
}
