package com.reservacanchas.repository;

import com.reservacanchas.model.EventoPartido;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.enums.EstadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Long> {

    List<EventoPartido> findByPartidoId(Long partidoId);

    @Query("SELECT g FROM Gol g WHERE g.estado = :estado AND g.fechaLimiteAtribucion < :ahora")
    List<Gol> findGolesVencidos(@Param("estado") EstadoEvento estado,
                                @Param("ahora") LocalDateTime ahora);

    @Query("SELECT g FROM Gol g WHERE g.estado = :estado AND g.anotador = :participante")
    List<Gol> findGolesConfirmadosPorAnotador(
            @Param("estado") EstadoEvento estado,
            @Param("participante") ParticipantePartido participante);

    @Query("SELECT g FROM Gol g WHERE g.estado = :estado AND g.asistente = :participante")
    List<Gol> findGolesConfirmadosPorAsistente(
            @Param("estado") EstadoEvento estado,
            @Param("participante") ParticipantePartido participante);

    @Query("SELECT g FROM Gol g WHERE g.estado = :estado AND g.anotador.persona.id = :personaId")
    List<Gol> findGolesConfirmadosPorPersonaId(
            @Param("estado") EstadoEvento estado,
            @Param("personaId") Long personaId);

    @Query("SELECT g FROM Gol g WHERE g.estado = :estado AND g.asistente.persona.id = :personaId")
    List<Gol> findAsistenciasConfirmadasPorPersonaId(
            @Param("estado") EstadoEvento estado,
            @Param("personaId") Long personaId);

    @Modifying
    @Query("UPDATE EventoPartido e SET e.anotador = null WHERE e.anotador = :participante")
    int limpiarAnotador(@Param("participante") ParticipantePartido participante);

    @Modifying
    @Query("UPDATE EventoPartido e SET e.asistente = null WHERE e.asistente = :participante")
    int limpiarAsistente(@Param("participante") ParticipantePartido participante);

    @Query("SELECT COUNT(g) FROM Gol g " +
           "WHERE g.estado = :estado " +
           "AND g.anotador.persona.id = :personaId " +
           "AND (:sedeId IS NULL OR g.partido.reserva.cancha.sede.id = :sedeId) " +
           "AND (:desde IS NULL OR g.partido.reserva.fecha >= :desde) " +
           "AND (:hasta IS NULL OR g.partido.reserva.fecha <= :hasta)")
    long contarGoles(@Param("estado") EstadoEvento estado,
                     @Param("personaId") Long personaId,
                     @Param("sedeId") Long sedeId,
                     @Param("desde") LocalDate desde,
                     @Param("hasta") LocalDate hasta);

    @Query("SELECT COUNT(g) FROM Gol g " +
           "WHERE g.estado = :estado " +
           "AND g.asistente.persona.id = :personaId " +
           "AND (:sedeId IS NULL OR g.partido.reserva.cancha.sede.id = :sedeId) " +
           "AND (:desde IS NULL OR g.partido.reserva.fecha >= :desde) " +
           "AND (:hasta IS NULL OR g.partido.reserva.fecha <= :hasta)")
    long contarAsistencias(@Param("estado") EstadoEvento estado,
                           @Param("personaId") Long personaId,
                           @Param("sedeId") Long sedeId,
                           @Param("desde") LocalDate desde,
                           @Param("hasta") LocalDate hasta);

    @Query("SELECT g.anotador.persona.id, COUNT(g) FROM Gol g " +
           "WHERE g.estado = :estado " +
           "AND g.anotador IS NOT NULL " +
           "AND (:sedeId IS NULL OR g.partido.reserva.cancha.sede.id = :sedeId) " +
           "AND (:desde IS NULL OR g.partido.reserva.fecha >= :desde) " +
           "AND (:hasta IS NULL OR g.partido.reserva.fecha <= :hasta) " +
           "GROUP BY g.anotador.persona.id " +
           "ORDER BY COUNT(g) DESC")
    List<Object[]> rankingGoles(@Param("estado") EstadoEvento estado,
                                @Param("sedeId") Long sedeId,
                                @Param("desde") LocalDate desde,
                                @Param("hasta") LocalDate hasta);

    @Query("SELECT g.asistente.persona.id, COUNT(g) FROM Gol g " +
           "WHERE g.estado = :estado " +
           "AND g.asistente IS NOT NULL " +
           "AND (:sedeId IS NULL OR g.partido.reserva.cancha.sede.id = :sedeId) " +
           "AND (:desde IS NULL OR g.partido.reserva.fecha >= :desde) " +
           "AND (:hasta IS NULL OR g.partido.reserva.fecha <= :hasta) " +
           "GROUP BY g.asistente.persona.id " +
           "ORDER BY COUNT(g) DESC")
    List<Object[]> rankingAsistencias(@Param("estado") EstadoEvento estado,
                                      @Param("sedeId") Long sedeId,
                                      @Param("desde") LocalDate desde,
                                      @Param("hasta") LocalDate hasta);
}
