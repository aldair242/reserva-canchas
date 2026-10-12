package com.reservacanchas.service;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.dto.dispositivo.RegistroCamaraRequest;
import com.reservacanchas.dto.dispositivo.RegistroSensorRequest;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface DispositivoService {

    DispositivoRegistroResponse registrarSensor(Long canchaId, RegistroSensorRequest request, Authentication authentication);

    DispositivoRegistroResponse registrarCamara(Long canchaId, RegistroCamaraRequest request, Authentication authentication);

    DispositivoResponse desactivar(Long id, Authentication authentication);

    List<DispositivoResponse> listarPorCancha(Long canchaId, Authentication authentication);

    DispositivoResponse obtenerPorId(Long id, Authentication authentication);
}

