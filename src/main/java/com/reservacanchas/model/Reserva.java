package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoReserva;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "reservas")
@Getter
@Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado;

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    @Version
    @Column(nullable = false)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizador_id", nullable = false)
    private Jugador organizador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha_id", nullable = false)
    private Cancha cancha;

    @OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    @OneToOne(mappedBy = "reserva", fetch = FetchType.LAZY)
    private Partido partido;

    public void confirmar() {
        this.estado = EstadoReserva.CONFIRMADA;
    }

    public void cancelar(String motivo) {
        this.estado = EstadoReserva.CANCELADA;
        this.motivoCancelacion = motivo;
    }

    public void reprogramar(LocalDate f, LocalTime ini, LocalTime fin) {
        this.fecha = f;
        this.horaInicio = ini;
        this.horaFin = fin;
        this.precioTotal = calcularMonto();
    }

    public BigDecimal calcularMonto() {
        if (cancha == null) {
            return BigDecimal.ZERO;
        }
        return cancha.calcularPrecio(fecha, horaInicio, horaFin);
    }

    public boolean puedeCancelarse() {
        if (estado == EstadoReserva.CANCELADA || estado == EstadoReserva.COMPLETADA
                || estado == EstadoReserva.NO_SHOW) {
            return false;
        }
        LocalDateTime inicio = LocalDateTime.of(fecha, horaInicio);
        return LocalDateTime.now().isBefore(inicio.minusHours(2));
    }

    public boolean estaPagada() {
        return pagos.stream().anyMatch(p -> p.getEstado() == com.reservacanchas.model.enums.EstadoPago.PAGADO);
    }

    public void agregarPago(Pago p) {
        pagos.add(p);
        p.setReserva(this);
    }

    public String generarCodigo() {
        this.codigo = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return this.codigo;
    }
}
