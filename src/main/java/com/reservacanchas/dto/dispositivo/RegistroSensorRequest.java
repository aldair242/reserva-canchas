package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Solicitud para registrar un sensor de arco en una cancha")
public record RegistroSensorRequest(
        @Schema(description = "Código identificador del sensor", example = "SN-ARCO-NORTE-1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "El codigo es obligatorio")
        String codigo,

        @Schema(description = "Ubicación del arco donde se instala el sensor", example = "ARCO_NORTE", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "La ubicacion del arco es obligatoria")
        UbicacionSensor ubicacion,

        @Schema(description = "Sensibilidad del sensor (0-100)", example = "80")
        Integer sensibilidad
) {
}

