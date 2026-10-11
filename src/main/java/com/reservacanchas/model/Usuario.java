package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
public class Usuario extends Persona implements IAutenticable {

    @Column(nullable = false, unique = true, length = 150)
    private String correo;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    private boolean activo;

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Override
    public boolean autenticar(String password) {
        return activo && ENCODER.matches(password, passwordHash);
    }

    @Override
    public void cambiarPassword(String actual, String nueva) {
        if (!autenticar(actual)) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }
        this.passwordHash = ENCODER.encode(nueva);
    }

    public void desactivar() {
        this.activo = false;
    }

    @Override
    public String getNombreVisible() {
        return getNombreCompleto();
    }
}
