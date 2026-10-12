package com.reservacanchas.controller;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.dto.dispositivo.RegistroCamaraRequest;
import com.reservacanchas.dto.dispositivo.RegistroSensorRequest;
import com.reservacanchas.service.DispositivoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Dispositivos", description = "Gestion y registro de sensores y camaras en canchas deportivas")
@SecurityRequirement(name = "bearerAuth")
public class DispositivoController {

    private final DispositivoService dispositivoService;

    public DispositivoController(DispositivoService dispositivoService) {
        this.dispositivoService = dispositivoService;
    }

    @PostMapping("/api/canchas/{canchaId}/sensores")
    @Operation(summary = "Registrar un sensor de arco en una cancha", description = "Registra un sensor de arco y genera una clave API de acceso unico en texto claro.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sensor registrado exitosamente con clave API visible una unica vez"),
            @ApiResponse(responseCode = "400", description = "Datos de sensor invalidos o codigo duplicado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar la cancha"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<DispositivoRegistroResponse> registrarSensor(
            @PathVariable Long canchaId,
            @Valid @RequestBody RegistroSensorRequest request,
            Authentication authentication) {
        DispositivoRegistroResponse response = dispositivoService.registrarSensor(canchaId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/api/canchas/{canchaId}/camaras")
    @Operation(summary = "Registrar una camara en una cancha", description = "Registra una camara para transmision y grabacion de video.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Camara registrada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de camara invalidos o codigo duplicado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar la cancha"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<DispositivoRegistroResponse> registrarCamara(
            @PathVariable Long canchaId,
            @Valid @RequestBody RegistroCamaraRequest request,
            Authentication authentication) {
        DispositivoRegistroResponse response = dispositivoService.registrarCamara(canchaId, request, authentication);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/api/dispositivos/{id}/desactivar")
    @Operation(summary = "Desactivar un dispositivo", description = "Inhabilita un sensor o camara para que no pueda enviar eventos.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispositivo desactivado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado para administrar el dispositivo"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado")
    })
    public ResponseEntity<DispositivoResponse> desactivar(
            @PathVariable Long id,
            Authentication authentication) {
        DispositivoResponse response = dispositivoService.desactivar(id, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/canchas/{canchaId}/dispositivos")
    @Operation(summary = "Listar dispositivos de una cancha", description = "Retorna todos los sensores y camaras instalados en la cancha indicada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de dispositivos obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Cancha no encontrada")
    })
    public ResponseEntity<List<DispositivoResponse>> listarPorCancha(
            @PathVariable Long canchaId,
            Authentication authentication) {
        List<DispositivoResponse> response = dispositivoService.listarPorCancha(canchaId, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/dispositivos/{id}")
    @Operation(summary = "Obtener informacion de un dispositivo", description = "Retorna los datos de configuracion y estado en linea de un dispositivo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dispositivo obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "404", description = "Dispositivo no encontrado")
    })
    public ResponseEntity<DispositivoResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        DispositivoResponse response = dispositivoService.obtenerPorId(id, authentication);
        return ResponseEntity.ok(response);
    }
}

