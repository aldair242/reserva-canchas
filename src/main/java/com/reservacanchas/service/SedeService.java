package com.reservacanchas.service;

import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface SedeService {

    SedeResponse crear(SedeRequest request, Authentication authentication);

    SedeResponse obtenerPorId(Long id);

    List<SedeResponse> listarTodas();

    SedeResponse actualizar(Long id, SedeRequest request, Authentication authentication);

    void eliminar(Long id, Authentication authentication);

    List<SedeResponse> listarMisSedes(Authentication authentication);
}
