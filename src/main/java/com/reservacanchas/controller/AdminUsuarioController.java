package com.reservacanchas.controller;

import com.reservacanchas.dto.admin.AdministradorSedeResponse;
import com.reservacanchas.dto.admin.CrearAdminSedeRequest;
import com.reservacanchas.service.AdminUsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/usuarios")
@Tag(name = "Administración de Usuarios", description = "Endpoints para la gestión y creación de administradores de sede por el Administrador General")
@SecurityRequirement(name = "bearerAuth")
public class AdminUsuarioController {

    private final AdminUsuarioService adminUsuarioService;

    public AdminUsuarioController(AdminUsuarioService adminUsuarioService) {
        this.adminUsuarioService = adminUsuarioService;
    }

    @GetMapping("/administradores-sede")
    @Operation(summary = "Listar administradores de sede", description = "Obtiene la lista de todos los usuarios con rol Administrador de Sede y sus sedes asociadas.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de administradores de sede obtenida exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ADMIN_SEDE')")
    public ResponseEntity<List<AdministradorSedeResponse>> listarAdministradoresSede() {
        return ResponseEntity.ok(adminUsuarioService.listarAdministradoresSede());
    }

    @PostMapping("/administradores-sede")
    @Operation(summary = "Crear nuevo Administrador de Sede", description = "Permite al Administrador General registrar un nuevo usuario con rol de Administrador de Sede.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Administrador de Sede creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo ya registrado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso restringido al Administrador General")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministradorSedeResponse> crearAdministradorSede(
            @Valid @RequestBody CrearAdminSedeRequest request) {
        AdministradorSedeResponse response = adminUsuarioService.crearAdministradorSede(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/administradores-sede/{id}")
    @Operation(summary = "Modificar Administrador de Sede", description = "Permite al Administrador General actualizar datos o credenciales de un Administrador de Sede.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Administrador de Sede actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o correo duplicado"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso restringido al Administrador General"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdministradorSedeResponse> actualizarAdministradorSede(
            @PathVariable Long id,
            @Valid @RequestBody com.reservacanchas.dto.admin.ActualizarAdminSedeRequest request) {
        AdministradorSedeResponse response = adminUsuarioService.actualizarAdministradorSede(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/administradores-sede/{id}")
    @Operation(summary = "Eliminar Administrador de Sede", description = "Permite al Administrador General dar de baja un Administrador de Sede desvinculando sus sedes.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Administrador de Sede eliminado exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "403", description = "Acceso restringido al Administrador General"),
            @ApiResponse(responseCode = "404", description = "Administrador no encontrado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarAdministradorSede(@PathVariable Long id) {
        adminUsuarioService.eliminarAdministradorSede(id);
        return ResponseEntity.noContent().build();
    }
}

