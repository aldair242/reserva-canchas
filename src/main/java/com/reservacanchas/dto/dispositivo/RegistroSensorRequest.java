package com.reservacanchas.dto.dispositivo;

import com.reservacanchas.model.enums.UbicacionSensor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroSensorRequest(
        @NotBlank(message = "El codigo es obligatorio")
        String codigo,

        @NotNull(message = "La ubicacion del arco es obligatoria")
        UbicacionSensor ubicacion,

        Integer sensibilidad
) {
}

