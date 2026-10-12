package com.reservacanchas.controller;

import com.reservacanchas.dto.clip.ClipDetalleResponse;
import com.reservacanchas.dto.clip.ClipResponse;
import com.reservacanchas.service.ClipService;
import com.reservacanchas.service.IAlmacenamiento;
import com.reservacanchas.service.impl.AlmacenamientoLocal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@RestController
@Tag(name = "Clips", description = "Endpoints de highlights y clips de partidos")
@SecurityRequirement(name = "bearerAuth")
public class ClipController {

    private final ClipService clipService;
    private final IAlmacenamiento almacenamiento;

    public ClipController(ClipService clipService, IAlmacenamiento almacenamiento) {
        this.clipService = clipService;
        this.almacenamiento = almacenamiento;
    }

    @GetMapping("/api/partidos/{id}/clips")
    @Operation(summary = "Listar clips de un partido (solo participantes y administradores)")
    public ResponseEntity<List<ClipResponse>> listarPorPartido(
            @PathVariable Long id,
            Authentication authentication) {
        List<ClipResponse> clips = clipService.listarClipsPartido(id, authentication);
        return ResponseEntity.ok(clips);
    }

    @GetMapping("/api/clips/{id}")
    @Operation(summary = "Obtener un clip por id con URL firmada o servida (incrementa vistas)")
    public ResponseEntity<ClipDetalleResponse> obtenerPorId(
            @PathVariable Long id,
            Authentication authentication) {
        ClipDetalleResponse clip = clipService.obtenerClip(id, authentication);
        return ResponseEntity.ok(clip);
    }

    @PutMapping("/api/clips/{id}/descartar")
    @Operation(summary = "Descartar un clip (no se mostrara en el listado)")
    public ResponseEntity<ClipResponse> descartar(
            @PathVariable Long id,
            Authentication authentication) {
        ClipResponse response = clipService.descartarClip(id, authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/clips/{id}/reintentar")
    @Operation(summary = "Reintentar la generacion de un clip en estado ERROR")
    public ResponseEntity<ClipResponse> reintentar(
            @PathVariable Long id,
            Authentication authentication) {
        ClipResponse response = clipService.reintentarClip(id, authentication);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/clips/archivos/{nombreArchivo:.+}")
    @Operation(summary = "Descargar o reproducir archivo multimedia almacenado localmente")
    public ResponseEntity<Resource> servirArchivo(
            @PathVariable String nombreArchivo,
            @RequestParam(name = "expires", required = false) Long expires,
            @RequestParam(name = "signature", required = false) String signature) {
        if (!almacenamiento.validarFirma(nombreArchivo, expires, signature)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        if (almacenamiento instanceof AlmacenamientoLocal local) {
            File archivo = Path.of(local.getDirectorio(), nombreArchivo).toFile();
            if (archivo.exists()) {
                Resource resource = new FileSystemResource(archivo);
                MediaType mediaType = nombreArchivo.endsWith(".mp4")
                        ? MediaType.valueOf("video/mp4")
                        : (nombreArchivo.endsWith(".jpg") || nombreArchivo.endsWith(".jpeg")
                        ? MediaType.IMAGE_JPEG : MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + archivo.getName() + "\"")
                        .body(resource);
            }
        }
        return ResponseEntity.notFound().build();
    }
}

