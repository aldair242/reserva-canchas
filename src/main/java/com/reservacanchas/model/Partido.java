package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "partidos")
@Getter
@Setter
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPartido estado;

    @Column(name = "nombre_equipo_a", length = 100)
    private String nombreEquipoA;

    @Column(name = "nombre_equipo_b", length = 100)
    private String nombreEquipoB;

    @Column(name = "goles_equipo_a", nullable = false)
    private int golesEquipoA;

    @Column(name = "goles_equipo_b", nullable = false)
    private int golesEquipoB;

    @Column(name = "inicio_real")
    private LocalDateTime inicioReal;

    @Column(name = "fin_real")
    private LocalDateTime finReal;

    @Column(name = "token_invitacion", nullable = false, unique = true, length = 100)
    private String tokenInvitacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipantePartido> participantes = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EventoPartido> eventos = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grabacion> grabaciones = new ArrayList<>();

    @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clip> clips = new ArrayList<>();

    public void iniciar() {
        this.estado = EstadoPartido.EN_JUEGO;
        this.inicioReal = LocalDateTime.now();
    }

    public void finalizar() {
        this.estado = EstadoPartido.FINALIZADO;
        this.finReal = LocalDateTime.now();
        recalcularMarcador();
    }

    public void agregarParticipante(ParticipantePartido p) {
        participantes.add(p);
        p.setPartido(this);
    }

    public void registrarEvento(EventoPartido e) {
        eventos.add(e);
        e.setPartido(this);
    }

    public void recalcularMarcador() {
        int golesA = 0;
        int golesB = 0;
        for (EventoPartido e : eventos) {
            if (e.getEstado() == EstadoEvento.CONFIRMADO && e.afectaMarcador()) {
                if (e.getEquipo() == LadoEquipo.A) {
                    golesA++;
                } else {
                    golesB++;
                }
            }
        }
        this.golesEquipoA = golesA;
        this.golesEquipoB = golesB;
    }

    public LadoEquipo obtenerGanador() {
        if (golesEquipoA == golesEquipoB) {
            return null;
        }
        return golesEquipoA > golesEquipoB ? LadoEquipo.A : LadoEquipo.B;
    }

    public String generarEnlaceInvitacion() {
        this.tokenInvitacion = "INV-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase();
        return this.tokenInvitacion;
    }
}
