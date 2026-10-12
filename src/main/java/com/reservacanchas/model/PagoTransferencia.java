package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pagos_transferencia")
@Getter
@Setter
public class PagoTransferencia extends Pago {

    @Column(length = 50)
    private String banco;

    @Column(name = "codigo_operacion", length = 100)
    private String codigoOperacion;

    @Override
    public boolean procesar() {
        // Simulación de procesamiento de transferencia bancaria
        if (banco == null || banco.isBlank()) {
            marcarFallido();
            return false;
        }
        confirmar();
        return true;
    }
}
