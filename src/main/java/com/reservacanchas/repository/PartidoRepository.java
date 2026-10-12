package com.reservacanchas.repository;

import com.reservacanchas.model.Partido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {

    Optional<Partido> findByReservaId(Long reservaId);

    Optional<Partido> findByTokenInvitacion(String tokenInvitacion);

    @Query("SELECT p FROM Partido p WHERE p.reserva.id = :reservaId")
    Optional<Partido> buscarPorReservaId(@Param("reservaId") Long reservaId);

    @Query("SELECT p FROM Partido p WHERE p.reserva.cancha.id = :canchaId AND p.estado = :estado")
    Optional<Partido> findByCanchaIdAndEstado(@Param("canchaId") Long canchaId, @Param("estado") com.reservacanchas.model.enums.EstadoPartido estado);

    @Query("SELECT DISTINCT p FROM Partido p " +
           "LEFT JOIN p.reserva r " +
           "LEFT JOIN p.participantes part " +
           "WHERE r.organizador.id = :jugadorId OR part.persona.id = :jugadorId " +
           "ORDER BY p.id DESC")
    java.util.List<Partido> findMisPartidos(@Param("jugadorId") Long jugadorId);
}
