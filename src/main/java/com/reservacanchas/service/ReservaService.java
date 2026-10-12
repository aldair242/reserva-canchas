package com.reservacanchas.service;

import com.reservacanchas.dto.reserva.CancelarRequest;
import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.dto.reserva.ReprogramarRequest;
import com.reservacanchas.dto.reserva.ReservaRequest;
import com.reservacanchas.dto.reserva.ReservaResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ReservaService {

    ReservaResponse crear(ReservaRequest request, Authentication authentication);

    List<ReservaResponse> listarMisReservas(Authentication authentication);

    ReservaResponse cancelar(Long id, CancelarRequest request, Authentication authentication);

    ReservaResponse reprogramar(Long id, ReprogramarRequest request, Authentication authentication);

    ReservaResponse pagar(Long id, PagoRequest request, Authentication authentication);
}
