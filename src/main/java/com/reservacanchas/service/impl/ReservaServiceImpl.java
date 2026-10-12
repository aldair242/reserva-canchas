package com.reservacanchas.service.impl;

import com.reservacanchas.dto.reserva.CancelarRequest;
import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.dto.reserva.ReprogramarRequest;
import com.reservacanchas.dto.reserva.ReservaRequest;
import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ReservaConflictException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.ReservaMapper;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Pago;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.model.enums.EstadoReserva;
import com.reservacanchas.repository.CanchaRepository;
import com.reservacanchas.repository.ReservaRepository;
import com.reservacanchas.service.PagoService;
import com.reservacanchas.service.ReservaService;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final List<EstadoReserva> ESTADOS_ACTIVOS =
            List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA);

    private final ReservaRepository reservaRepository;
    private final CanchaRepository canchaRepository;
    private final ReservaMapper reservaMapper;
    private final SecurityService securityService;
    private final PagoService pagoService;

    public ReservaServiceImpl(ReservaRepository reservaRepository,
                              CanchaRepository canchaRepository,
                              ReservaMapper reservaMapper,
                              SecurityService securityService,
                              PagoService pagoService) {
        this.reservaRepository = reservaRepository;
        this.canchaRepository = canchaRepository;
        this.reservaMapper = reservaMapper;
        this.securityService = securityService;
        this.pagoService = pagoService;
    }

    @Override
    @Transactional
    public ReservaResponse crear(ReservaRequest request, Authentication authentication) {
        Jugador organizador = obtenerJugadorAutenticado(authentication);

        Cancha cancha = canchaRepository.findByIdForUpdate(request.canchaId())
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", request.canchaId()));

        validarHorario(request.fecha(), request.horaInicio(), request.horaFin(), cancha);

        List<Reserva> solapadas = reservaRepository.buscarSolapadas(
                cancha.getId(), request.fecha(), request.horaInicio(), request.horaFin(), ESTADOS_ACTIVOS);
        if (!solapadas.isEmpty()) {
            throw new ReservaConflictException(
                    "La cancha ya está reservada en ese horario o se está reservando en este momento, intenta de nuevo");
        }

        Reserva reserva = new Reserva();
        reserva.setOrganizador(organizador);
        reserva.setCancha(cancha);
        reserva.setFecha(request.fecha());
        reserva.setHoraInicio(request.horaInicio());
        reserva.setHoraFin(request.horaFin());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setPrecioTotal(cancha.calcularPrecio(request.fecha(), request.horaInicio(), request.horaFin()));
        reserva.generarCodigo();

        Reserva guardada = reservaRepository.saveAndFlush(reserva);
        return reservaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservaResponse> listarMisReservas(Authentication authentication) {
        Jugador organizador = obtenerJugadorAutenticado(authentication);
        return reservaRepository.findByOrganizadorIdOrderByFechaDescHoraInicioDesc(organizador.getId())
                .stream()
                .map(reservaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public ReservaResponse cancelar(Long id, CancelarRequest request, Authentication authentication) {
        Reserva reserva = obtenerReservaPropia(id, authentication);

        if (!reserva.puedeCancelarse()) {
            throw new OperacionNoPermitidaException(
                    "No se puede cancelar la reserva: faltan menos de 2 horas para el inicio o el estado no lo permite");
        }

        reserva.cancelar(request.motivo());
        Reserva guardada = reservaRepository.save(reserva);
        return reservaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public ReservaResponse reprogramar(Long id, ReprogramarRequest request, Authentication authentication) {
        Reserva reserva = obtenerReservaPropia(id, authentication);

        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.COMPLETADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new OperacionNoPermitidaException(
                    "No se puede reprogramar una reserva en estado " + reserva.getEstado());
        }

        Cancha cancha = canchaRepository.findByIdForUpdate(reserva.getCancha().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", reserva.getCancha().getId()));
        validarHorario(request.fecha(), request.horaInicio(), request.horaFin(), cancha);

        List<Reserva> solapadas = reservaRepository.buscarSolapadasExcluyendo(
                cancha.getId(), request.fecha(), request.horaInicio(), request.horaFin(),
                ESTADOS_ACTIVOS, id);
        if (!solapadas.isEmpty()) {
            throw new ReservaConflictException(
                    "La cancha ya está reservada en ese horario o se está reservando en este momento, intenta de nuevo");
        }

        reserva.reprogramar(request.fecha(), request.horaInicio(), request.horaFin());
        Reserva guardada = reservaRepository.saveAndFlush(reserva);
        return reservaMapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public ReservaResponse pagar(Long id, PagoRequest request, Authentication authentication) {
        Reserva reserva = obtenerReservaPropia(id, authentication);

        if (reserva.getEstado() == EstadoReserva.CANCELADA
                || reserva.getEstado() == EstadoReserva.COMPLETADA
                || reserva.getEstado() == EstadoReserva.NO_SHOW) {
            throw new OperacionNoPermitidaException(
                    "No se puede pagar una reserva en estado " + reserva.getEstado());
        }

        Pago pago = pagoService.procesarPago(reserva, request);
        if (pago.getEstado() == com.reservacanchas.model.enums.EstadoPago.PAGADO) {
            reserva.confirmar();
        }

        Reserva guardada = reservaRepository.save(reserva);
        return reservaMapper.toResponse(guardada);
    }

    private void validarHorario(LocalDate fecha, LocalTime inicio, LocalTime fin, Cancha cancha) {
        if (!fin.isAfter(inicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio");
        }
        Sede sede = cancha.getSede();
        if (sede == null) {
            throw new IllegalArgumentException("La cancha no tiene sede asociada");
        }
        if (!sede.estaAbiertaEn(inicio) || !sede.estaAbiertaEn(fin)) {
            throw new IllegalArgumentException(
                    "El horario solicitado esta fuera del horario de atencion de la sede");
        }
        if (!cancha.estaDisponible(fecha, inicio, fin)) {
            throw new IllegalArgumentException(
                    "La cancha esta bloqueada en el horario solicitado");
        }
    }

    private Jugador obtenerJugadorAutenticado(Authentication authentication) {
        Usuario usuario = securityService.getUsuarioActual(authentication);
        if (!(usuario instanceof Jugador jugador)) {
            throw new OperacionNoPermitidaException("Solo los jugadores pueden realizar reservas");
        }
        return jugador;
    }

    private Reserva obtenerReservaPropia(Long id, Authentication authentication) {
        Jugador organizador = obtenerJugadorAutenticado(authentication);
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        if (!reserva.getOrganizador().getId().equals(organizador.getId())) {
            throw new OperacionNoPermitidaException(
                    "No tiene permisos para operar sobre esta reserva");
        }
        return reserva;
    }
}
