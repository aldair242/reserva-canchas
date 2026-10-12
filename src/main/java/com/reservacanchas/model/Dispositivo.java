package com.reservacanchas.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
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
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispositivos")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "tipo_dispositivo")
@Getter
@Setter
public abstract class Dispositivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false, unique = true, length = 50)
    protected String codigo;

    @Column(name = "clave_api_hash", nullable = false)
    protected String claveApiHash;

    @Column(name = "ultimo_latido")
    protected LocalDateTime ultimoLatido;

    @Column(nullable = false)
    protected boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancha_id", nullable = false)
    protected Cancha cancha;

    public abstract String getTipo();

    public boolean validarClave(String clave) {
        if (clave == null || this.claveApiHash == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(clave, this.claveApiHash);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void registrarLatido() {
        this.ultimoLatido = LocalDateTime.now();
    }

    public boolean estaEnLinea() {
        return this.ultimoLatido != null
                && this.ultimoLatido.isAfter(LocalDateTime.now().minusMinutes(2));
    }
}

