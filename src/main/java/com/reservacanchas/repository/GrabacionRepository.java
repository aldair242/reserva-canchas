package com.reservacanchas.repository;

import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.enums.EstadoGrabacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GrabacionRepository extends JpaRepository<Grabacion, Long> {

    Optional<Grabacion> findFirstByPartidoIdAndEstado(Long partidoId, EstadoGrabacion estado);

    List<Grabacion> findByPartidoId(Long partidoId);

    List<Grabacion> findByPartidoIdAndEstado(Long partidoId, EstadoGrabacion estado);
}

