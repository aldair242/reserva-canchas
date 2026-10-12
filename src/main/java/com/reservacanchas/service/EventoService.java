package com.reservacanchas.service;

import com.reservacanchas.dto.partido.AtribuirRequest;
import com.reservacanchas.dto.partido.EventoRequest;
import com.reservacanchas.dto.partido.EventoResponse;
import org.springframework.security.core.Authentication;

public interface EventoService {

    EventoResponse registrarEvento(Long partidoId, EventoRequest request, Authentication authentication);

    EventoResponse atribuir(Long eventoId, AtribuirRequest request, Authentication authentication);

    EventoResponse confirmar(Long eventoId, Authentication authentication);

    EventoResponse descartar(Long eventoId, Authentication authentication);
}
