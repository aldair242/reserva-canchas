package com.reservacanchas.service;

import com.reservacanchas.dto.partido.AtribuirRequest;
import com.reservacanchas.dto.partido.EventoRequest;
import com.reservacanchas.dto.partido.EventoResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface EventoService {

    EventoResponse registrarEvento(Long partidoId, EventoRequest request, Authentication authentication);

    List<EventoResponse> listarPorPartido(Long partidoId);

    EventoResponse atribuir(Long eventoId, AtribuirRequest request, Authentication authentication);

    EventoResponse confirmar(Long eventoId, Authentication authentication);

    EventoResponse descartar(Long eventoId, Authentication authentication);
}
