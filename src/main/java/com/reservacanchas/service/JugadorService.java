package com.reservacanchas.service;

import com.reservacanchas.dto.JugadorResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface JugadorService {

    JugadorResponse obtenerPorId(Long id);

    JugadorResponse obtenerPerfilActual(Authentication authentication);

    List<JugadorResponse> listarTodos();
}
