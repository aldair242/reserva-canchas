package com.reservacanchas.service;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.sensor.EventoSensorRequest;
import com.reservacanchas.dto.sensor.LatidoRequest;
import com.reservacanchas.dto.sensor.LatidoResponse;

public interface SensorService {

    EventoResponse procesarEvento(EventoSensorRequest request);

    LatidoResponse registrarLatido(LatidoRequest request);
}

