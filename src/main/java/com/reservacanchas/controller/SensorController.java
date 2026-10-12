package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.sensor.EventoSensorRequest;
import com.reservacanchas.dto.sensor.LatidoRequest;
import com.reservacanchas.dto.sensor.LatidoResponse;
import com.reservacanchas.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sensores")
@Tag(name = "Sensores", description = "Endpoints para eventos y latidos de sensores y dispositivos")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/eventos")
    @Operation(summary = "Registrar evento de gol desde un sensor de arco (autenticacion por codigo y clave API)")
    public ResponseEntity<EventoResponse> registrarEvento(@Valid @RequestBody EventoSensorRequest request) {
        EventoResponse response = sensorService.procesarEvento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/latido")
    @Operation(summary = "Registrar latido de vida de un dispositivo (sensor o camara)")
    public ResponseEntity<LatidoResponse> registrarLatido(@RequestBody LatidoRequest request) {
        LatidoResponse response = sensorService.registrarLatido(request);
        return ResponseEntity.ok(response);
    }
}

