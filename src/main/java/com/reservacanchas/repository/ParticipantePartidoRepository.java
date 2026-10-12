package com.reservacanchas.repository;

import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Persona;
import com.reservacanchas.model.enums.EstadoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ParticipantePartidoRepository extends JpaRepository<ParticipantePartido, Long> {

    List<ParticipantePartido> findByPartidoId(Long partidoId);

    List<ParticipantePartido> findByPersona(Persona persona);

    @Modifying
    @Query("UPDATE ParticipantePartido pp SET pp.persona = :nuevo WHERE pp.persona = :anterior")
    int reemplazarPersona(@Param("anterior") Persona anterior, @Param("nuevo") Persona nuevo);

    @Query("SELECT pp FROM ParticipantePartido pp " +
           "WHERE pp.persona.id = :personaId " +
           "AND pp.partido.estado = :estado")
    List<ParticipantePartido> findByPersonaIdAndPartidoEstado(
            @Param("personaId") Long personaId,
            @Param("estado") EstadoPartido estado);

    @Query("SELECT pp FROM ParticipantePartido pp " +
           "WHERE pp.persona.id = :personaId " +
           "AND pp.partido.estado = :estado " +
           "AND (:sedeId IS NULL OR pp.partido.reserva.cancha.sede.id = :sedeId) " +
           "AND (:desde IS NULL OR pp.partido.reserva.fecha >= :desde) " +
           "AND (:hasta IS NULL OR pp.partido.reserva.fecha <= :hasta)")
    List<ParticipantePartido> findPartidosJugados(
            @Param("personaId") Long personaId,
            @Param("estado") EstadoPartido estado,
            @Param("sedeId") Long sedeId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta);
}
