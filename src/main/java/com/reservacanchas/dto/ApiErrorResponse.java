package com.reservacanchas.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estructura estandar de error devuelta por la API")
public record ApiErrorResponse(
        @Schema(description = "Marca temporal del error", example = "2026-10-11T20:30:00")
        LocalDateTime timestamp,

        @Schema(description = "Codigo de estado HTTP", example = "400")
        int status,

        @Schema(description = "Descripcion del estado HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensaje explicativo del error", example = "Error de validacion")
        String message,

        @Schema(description = "Ruta del endpoint solicitado", example = "/api/reservas")
        String path,

        @Schema(description = "Detalle de errores por campo en validaciones")
        List<FieldError> fieldErrors
) {

    @Schema(description = "Detalle del error en un campo especifico")
    public record FieldError(
            @Schema(description = "Nombre del campo con error", example = "correo")
            String field,

            @Schema(description = "Descripcion del error de validacion", example = "El correo es obligatorio")
            String message
    ) {
    }
}
