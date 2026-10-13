package com.reservacanchas.service.impl;

import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.EstadoReserva;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartidoCicloVidaService {

    private static final Logger log = LoggerFactory.getLogger(PartidoCicloVidaService.class);

    private final ReservaRepository reservaRepository;
    private final PartidoRepository partidoRepository;
    private final CamaraCanchaRepository camaraCanchaRepository;
    private final GrabacionRepository grabacionRepository;

    public PartidoCicloVidaService(ReservaRepository reservaRepository,
                                  PartidoRepository partidoRepository,
                                  CamaraCanchaRepository camaraCanchaRepository,
                                  GrabacionRepository grabacionRepository) {
        this.reservaRepository = reservaRepository;
        this.partidoRepository = partidoRepository;
        this.camaraCanchaRepository = camaraCanchaRepository;
        this.grabacionRepository = grabacionRepository;
    }

    @Scheduled(fixedRateString = "${partidos.ciclo-vida.intervalo-ms:30000}")
    @Transactional
    public void procesarCicloDeVida() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Reserva> activas = reservaRepository.findByEstadoIn(
                List.of(EstadoReserva.PENDIENTE, EstadoReserva.CONFIRMADA));

        for (Reserva reserva : activas) {
            LocalDateTime inicio = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
            LocalDateTime fin = LocalDateTime.of(reserva.getFecha(), reserva.getHoraFin());

            // A) Si el horario ya terminó:
            if (!ahora.isBefore(fin)) {
                if (reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                    finalizarPartidoYReserva(reserva);
                } else if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
                    reserva.marcarNoShow();
                    reservaRepository.save(reserva);
                    log.info("Reserva {} marcada como NO_SHOW por haber finalizado sin confirmarse", reserva.getId());
                }
                continue;
            }

            // B) Si el horario ya inició pero aún no termina, y la reserva está CONFIRMADA:
            if (!ahora.isBefore(inicio) && ahora.isBefore(fin) && reserva.getEstado() == EstadoReserva.CONFIRMADA) {
                iniciarPartidoYGrabacion(reserva);
            }
        }
    }

    private void iniciarPartidoYGrabacion(Reserva reserva) {
        Partido partido = partidoRepository.findByReservaId(reserva.getId()).orElse(null);
        if (partido == null) {
            partido = new Partido();
            partido.setReserva(reserva);
            partido.setEstado(EstadoPartido.PROGRAMADO);
            partido.setNombreEquipoA("Equipo A");
            partido.setNombreEquipoB("Equipo B");
            partido.setGolesEquipoA(0);
            partido.setGolesEquipoB(0);
            partido.generarEnlaceInvitacion();

            ParticipantePartido participanteOrg = new ParticipantePartido();
            participanteOrg.setPersona(reserva.getOrganizador());
            participanteOrg.setEquipo(LadoEquipo.A);
            participanteOrg.setAsistio(true);
            partido.agregarParticipante(participanteOrg);
            partido = partidoRepository.save(partido);
        }

        if (partido.getEstado() == EstadoPartido.PROGRAMADO) {
            partido.iniciar();
            partido = partidoRepository.save(partido);
            log.info("Partido {} iniciado automáticamente para la reserva {}", partido.getId(), reserva.getId());

            // Iniciar grabación si la cancha tiene cámara activa y no hay grabación en curso
            if (reserva.getCancha() != null) {
                CamaraCancha camara = camaraCanchaRepository
                        .findFirstByCanchaIdAndActivoTrue(reserva.getCancha().getId())
                        .orElse(null);
                if (camara != null) {
                    boolean yaGrabando = !grabacionRepository
                            .findByPartidoIdAndEstado(partido.getId(), EstadoGrabacion.GRABANDO).isEmpty();
                    if (!yaGrabando) {
                        Grabacion grabacion = camara.iniciarGrabacion(partido);
                        grabacionRepository.save(grabacion);
                        log.info("Grabación iniciada automáticamente para el partido {}", partido.getId());
                    }
                }
            }
        }
    }

    private void finalizarPartidoYReserva(Reserva reserva) {
        Partido partido = partidoRepository.findByReservaId(reserva.getId()).orElse(null);
        if (partido != null) {
            if (partido.getEstado() == EstadoPartido.EN_JUEGO) {
                partido.finalizar();
                partidoRepository.save(partido);
                log.info("Partido {} finalizado automáticamente al término de la reserva {}", partido.getId(), reserva.getId());
            } else if (partido.getEstado() == EstadoPartido.PROGRAMADO) {
                partido.finalizar();
                partidoRepository.save(partido);
            }

            // Finalizar grabaciones activas
            List<Grabacion> grabacionesActivas = grabacionRepository
                    .findByPartidoIdAndEstado(partido.getId(), EstadoGrabacion.GRABANDO);
            for (Grabacion g : grabacionesActivas) {
                g.finalizar();
                grabacionRepository.save(g);
                log.info("Grabación {} finalizada automáticamente", g.getId());
            }
        }

        reserva.completar();
        reservaRepository.save(reserva);
        log.info("Reserva {} marcada como COMPLETADA automáticamente", reserva.getId());
    }
}
