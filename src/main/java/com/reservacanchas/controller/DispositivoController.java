package com.reservacanchas.controller;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.dto.dispositivo.RegistroCamaraRequest;
import com.reservacanchas.dto.dispositivo.RegistroSensorRequest;
import com.reservacanchas.service.DispositivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Dispositivos", description = "Gestion de sensores y camaras de las canchas")
@SecurityRequirement(name = "bearerAuth")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping("/api/canchas/{canchaId}/sensores")
    @Operation(summary = "Registrar un sensor de arco en una cancha")
    public ResponseEntity<DispositivoRegistroResponse> registrarSensor(
            @PathVariable Long canchaId,
            @Valid @RequestBody RegistroSensorRequest request,
            Authentication authentication) {
        DispositivoRegistroResponse response = dispositivoService.registrarSensor(canchaId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/canchas/{canchaId}/camaras")
    @Operation(summary = "Registrar una camara en una cancha")
    public ResponseEntity<DispositivoRegistroResponse> registrarCamara(
            @PathVariable Long canchaId,
            @Valid @RequestBody RegistroCamaraRequest request,
            Authentication authentication) {
        DispositivoRegistroResponse response = dispositivoService.registrarCamara(canchaId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/dispositivos/{id}/desactivar")
    @Operation(summary = "Desactivar un dispositivo")
    public ResponseEntity<DispositivoResponse> desactivar(
            @PathVariable Long id,
            Authentication authentication) {
        DispositivoResponse response = dispositivoService.desactivar(id, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/canchas/{canchaId}/dispositivos")
    @Operation(summary = "Listar dispositivos de una cancha")
    public ResponseEntity<List<DispositivoResponse>> listarPorCancha(
            @PathVariable Long canchaId,
            Authentication authentication) {
        List<DispositivoResponse> response = dispositivoService.listarPorCancha(canchaId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dispositivos/{id}")
    @Operation(summary = "Obtener informacion de un dispositivo")
    public ResponseEntity<DispositivoResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        DispositivoResponse response = dispositivoService.obtenerPorId(id, authentication);
        return ResponseEntity.ok(response);
    }
}

