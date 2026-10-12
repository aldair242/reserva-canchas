package com.reservacanchas.service.impl;

import com.reservacanchas.model.Gol;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.repository.EventoPartidoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventoCierreService {

    private static final Logger log = LoggerFactory.getLogger(EventoCierreService.class);

    private final EventoPartidoRepository eventoRepository;

    public EventoCierreService(EventoPartidoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    @Scheduled(fixedRateString = "${partidos.cierre.intervalo-ms:300000}")
    @Transactional
    public void cerrarEventosVencidos() {
        List<Gol> golesVencidos = eventoRepository.findGolesVencidos(
                EstadoEvento.PENDIENTE_ATRIBUCION, LocalDateTime.now());

        for (Gol gol : golesVencidos) {
            // Quitar anotador y asistente para que no cuente en estadisticas personales
            gol.setAnotador(null);
            gol.setAsistente(null);
            // Confirmar para que siga contando en el marcador
            gol.setEstado(EstadoEvento.CONFIRMADO);
            gol.setAtribuidor(null);
            gol.setConfirmador(null);
            eventoRepository.save(gol);
            log.info("Gol vencido confirmado como gol de equipo: eventoId={}", gol.getId());
        }

        if (!golesVencidos.isEmpty()) {
            log.info("Se cerraron {} eventos vencidos", golesVencidos.size());
        }
    }
}
