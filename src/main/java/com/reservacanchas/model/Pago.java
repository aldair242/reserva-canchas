package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoPago;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_pago")
@Getter
@Setter
public abstract class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    protected BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    protected EstadoPago estado;

    @Column(name = "fecha_pago")
    protected LocalDateTime fechaPago;

    @Column(length = 100)
    protected String referencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    protected Reserva reserva;

    public abstract boolean procesar();

    public void confirmar() {
        this.estado = EstadoPago.PAGADO;
        this.fechaPago = LocalDateTime.now();
    }

    public void marcarFallido() {
        this.estado = EstadoPago.FALLIDO;
    }

    public void reembolsar() {
        this.estado = EstadoPago.REEMBOLSADO;
    }
}
