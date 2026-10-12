package com.reservacanchas.model;

import com.reservacanchas.model.enums.EstadoGrabacion;
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
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grabaciones")
@Getter
@Setter
public class Grabacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url_base")
    private String urlBase;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column
    private LocalDateTime fin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGrabacion estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camara_id", nullable = false)
    private CamaraCancha camara;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;

    @OneToMany(mappedBy = "grabacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Clip> clips = new ArrayList<>();

    public void finalizar() {
        this.estado = EstadoGrabacion.FINALIZADA;
        this.fin = LocalDateTime.now();
    }

    public boolean contieneInstante(LocalDateTime t) {
        if (t == null || inicio == null) {
            return false;
        }
        boolean despuesDeInicio = !t.isBefore(inicio);
        boolean antesDeFin = (fin == null) || !t.isAfter(fin);
        return despuesDeInicio && antesDeFin;
    }
}

