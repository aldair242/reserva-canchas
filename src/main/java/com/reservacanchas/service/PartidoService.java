package com.reservacanchas.service;

import com.reservacanchas.dto.partido.ParticipanteRequest;
import com.reservacanchas.dto.partido.PartidoResponse;
import org.springframework.security.core.Authentication;

public interface PartidoService {

    PartidoResponse crearDesdeReserva(Long reservaId, Authentication authentication);

    PartidoResponse obtenerPorId(Long id);

    PartidoResponse agregarParticipante(Long partidoId, ParticipanteRequest request,
                                        Authentication authentication);

    PartidoResponse iniciar(Long id, Authentication authentication);

    PartidoResponse finalizar(Long id, Authentication authentication);
}
