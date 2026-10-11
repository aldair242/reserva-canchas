package com.reservacanchas.service;

import com.reservacanchas.dto.cancha.BloqueoCanchaRequest;
import com.reservacanchas.dto.cancha.CanchaRequest;
import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.dto.cancha.DisponibilidadResponse;
import com.reservacanchas.model.enums.TipoDeporte;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;

public interface CanchaService {

    CanchaResponse crear(CanchaRequest request, Authentication authentication);

    CanchaResponse obtenerPorId(Long id);

    List<CanchaResponse> listarTodas(TipoDeporte deporte, Long sedeId);

    CanchaResponse actualizar(Long id, CanchaRequest request, Authentication authentication);

    void eliminar(Long id, Authentication authentication);

    DisponibilidadResponse obtenerDisponibilidad(Long canchaId, LocalDate fecha);

    CanchaResponse bloquear(Long canchaId, BloqueoCanchaRequest request, Authentication authentication);
}
