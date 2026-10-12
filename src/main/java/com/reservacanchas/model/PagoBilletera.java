package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "pagos_billetera")
@Getter
@Setter
public class PagoBilletera extends Pago {

    @Column(length = 50)
    private String proveedor;

    @Column(name = "numero_operacion", length = 100)
    private String numeroOperacion;

    @Override
    public boolean procesar() {
        // Simulación de procesamiento con billetera digital
        if (proveedor == null || proveedor.isBlank()) {
            marcarFallido();
            return false;
        }
        confirmar();
        return true;
    }
}
