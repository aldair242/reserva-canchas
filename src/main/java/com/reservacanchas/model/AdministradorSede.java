package com.reservacanchas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "administradores_sede")
@Getter
@Setter
public class AdministradorSede extends Usuario {

    @Transient
    private List<Sede> sedes = new ArrayList<>();

    public void registrarCancha(Sede s, Cancha c) {
    }

    public void bloquearCancha(Cancha c, BloqueoCancha b) {
    }

    public List<Reserva> consultarReservas(Sede s, LocalDate f) {
        return List.of();
    }
}
