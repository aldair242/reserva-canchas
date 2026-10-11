package com.reservacanchas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter
@Setter
public class Administrador extends Usuario {

    public void crearSede(Sede s, AdministradorSede a) {
    }

    public void desactivarUsuario(Usuario u) {
        u.desactivar();
    }
}
