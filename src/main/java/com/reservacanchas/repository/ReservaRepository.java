package com.reservacanchas.repository;

import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByOrganizadorIdOrderByFechaDescHoraInicioDesc(Long organizadorId);

    @Query("SELECT r FROM Reserva r WHERE r.cancha.id = :canchaId " +
           "AND r.estado IN :estados " +
           "AND r.fecha = :fecha " +
           "AND r.horaInicio < :fin AND r.horaFin > :inicio")
    List<Reserva> buscarSolapadas(
            @Param("canchaId") Long canchaId,
            @Param("fecha") LocalDate fecha,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin,
            @Param("estados") List<EstadoReserva> estados);

    @Query("SELECT r FROM Reserva r WHERE r.cancha.id = :canchaId " +
           "AND r.estado IN :estados " +
           "AND r.fecha = :fecha " +
           "AND r.id <> :reservaId " +
           "AND r.horaInicio < :fin AND r.horaFin > :inicio")
    List<Reserva> buscarSolapadasExcluyendo(
            @Param("canchaId") Long canchaId,
            @Param("fecha") LocalDate fecha,
            @Param("inicio") LocalTime inicio,
            @Param("fin") LocalTime fin,
            @Param("estados") List<EstadoReserva> estados,
            @Param("reservaId") Long reservaId);

    @Query("SELECT r FROM Reserva r WHERE r.cancha.sede.id = :sedeId " +
           "AND r.fecha = :fecha " +
           "ORDER BY r.fecha DESC, r.horaInicio ASC")
    List<Reserva> buscarPorSedeYFechaExacta(
            @Param("sedeId") Long sedeId,
            @Param("fecha") LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.cancha.sede.id = :sedeId " +
           "ORDER BY r.fecha DESC, r.horaInicio ASC")
    List<Reserva> buscarPorSede(
            @Param("sedeId") Long sedeId);

    default List<Reserva> buscarPorSedeYFecha(Long sedeId, LocalDate fecha) {
        if (fecha == null) {
            return buscarPorSede(sedeId);
        }
        return buscarPorSedeYFechaExacta(sedeId, fecha);
    }

    List<Reserva> findByEstadoIn(List<EstadoReserva> estados);
}
