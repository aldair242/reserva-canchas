package com.reservacanchas.controller;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.sensor.EventoSensorRequest;
import com.reservacanchas.dto.sensor.LatidoRequest;
import com.reservacanchas.dto.sensor.LatidoResponse;
import com.reservacanchas.service.SensorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Sensores", description = "Endpoints de ingesta de telemetria y eventos desde dispositivos IoT (sensores y camaras)")
public class SensorController {

    private final SensorService sensorService;

    public SensorController(SensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping("/eventos")
    @Operation(summary = "Registrar evento de gol desde un sensor de arco", description = "Valida las credenciales de sensor (codigoSensor y claveApi), detecta el partido EN_JUEGO en esa cancha, registra el Gol en estado PENDIENTE_ATRIBUCION y dispara de forma asincrona la generacion del clip de video.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Evento de gol registrado y generacion de clip encolada"),
            @ApiResponse(responseCode = "400", description = "No hay partido en juego en la cancha del sensor"),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas del sensor o sensor inactivo")
    })
    public ResponseEntity<EventoResponse> registrarEvento(@Valid @RequestBody EventoSensorRequest request) {
        EventoResponse response = sensorService.procesarEvento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/latido")
    @Operation(summary = "Registrar latido de vida de un dispositivo", description = "Actualiza la marca temporal de ultimo latido del dispositivo para monitorear su estado en linea. Requiere codigoSensor y claveApi.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Latido registrado exitosamente"),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas del sensor o dispositivo inactivo")
    })
    public ResponseEntity<LatidoResponse> registrarLatido(@RequestBody LatidoRequest request) {
        LatidoResponse response = sensorService.registrarLatido(request);
        return ResponseEntity.ok(response);
    }
}

