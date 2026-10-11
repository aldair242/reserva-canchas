package com.reservacanchas.model;

import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import com.reservacanchas.model.tarifa.EstrategiaTarifa;
import com.reservacanchas.model.tarifa.TarifaFactory;
import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "canchas")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_cancha")
@Getter
@Setter
public abstract class Cancha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, length = 100)
    protected String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    protected TipoSuperficie superficie;

    @Column(nullable = false)
    protected boolean techada;

    @Column(nullable = false)
    protected boolean iluminacion;

    @Column(nullable = false)
    protected boolean activa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sede_id", nullable = false)
    protected Sede sede;

    @OneToMany(mappedBy = "cancha", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<BloqueoCancha> bloqueos = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarifa", length = 20)
    private TipoTarifa tipoTarifa;

    @Column(name = "precio_hora", precision = 10, scale = 2)
    private BigDecimal precioHora;

    @Column(name = "precio_normal", precision = 10, scale = 2)
    private BigDecimal precioNormal;

    @Column(name = "precio_pico", precision = 10, scale = 2)
    private BigDecimal precioPico;

    @Column(name = "inicio_pico")
    private LocalTime inicioPico;

    @Column(name = "fin_pico")
    private LocalTime finPico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarifa_base", length = 20)
    private TipoTarifa tipoTarifaBase;

    @Column(name = "porcentaje_descuento")
    private double porcentajeDescuento;

    @Column(name = "vigente_desde")
    private LocalDate vigenteDesde;

    @Column(name = "vigente_hasta")
    private LocalDate vigenteHasta;

    @Transient
    private EstrategiaTarifa tarifa;

    @PostLoad
    private void reconstruirTarifa() {
        this.tarifa = TarifaFactory.crear(
                tipoTarifa, precioHora, precioNormal, precioPico,
                inicioPico, finPico, tipoTarifaBase,
                porcentajeDescuento, vigenteDesde, vigenteHasta);
    }

    public void setTarifa(EstrategiaTarifa tarifa) {
        this.tarifa = tarifa;
    }

    public abstract TipoDeporte getDeporte();

    public abstract int getCapacidadJugadores();

    public BigDecimal calcularPrecio(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (tarifa == null) {
            reconstruirTarifa();
        }
        return tarifa.calcular(this, fecha, inicio, fin);
    }

    public boolean estaDisponible(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        if (sede == null || !sede.estaAbiertaEn(inicio) || !sede.estaAbiertaEn(fin)) {
            return false;
        }
        LocalDateTime ini = LocalDateTime.of(fecha, inicio);
        LocalDateTime finDt = LocalDateTime.of(fecha, fin);
        return bloqueos.stream().noneMatch(b -> b.seSolapaCon(ini, finDt));
    }

    public void bloquear(BloqueoCancha b) {
        bloqueos.add(b);
        b.setCancha(this);
    }
}
