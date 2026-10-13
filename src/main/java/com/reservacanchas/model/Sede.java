package com.reservacanchas.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sedes")
@Getter
@Setter
public class Sede {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 255)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String distrito;

    @Column
    private Double latitud;

    @Column
    private Double longitud;

    @Column(nullable = false)
    private LocalTime horaApertura;

    @Column(nullable = false)
    private LocalTime horaCierre;

    @Column(nullable = false)
    private boolean activa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administrador_sede_id")
    private AdministradorSede administradorSede;

    @OneToMany(mappedBy = "sede", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cancha> canchas = new ArrayList<>();

    public void agregarCancha(Cancha c) {
        canchas.add(c);
        c.setSede(this);
    }

    public boolean estaAbiertaEn(LocalTime hora) {
        if (horaApertura == null || horaCierre == null) {
            return false;
        }
        return !hora.isBefore(horaApertura) && !hora.isAfter(horaCierre);
    }
}
