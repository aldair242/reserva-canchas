package com.reservacanchas.dto.sensor;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payload para señal de latido (heartbeat) de un sensor o cámara")
public record LatidoRequest(
        @Schema(description = "Código identificador del dispositivo (cámara o sensor)", example = "SN-ARC-01")
        String codigo,

        @Schema(description = "Código alternativo del sensor", example = "SN-ARC-01")
        String codigoSensor,

        @Schema(description = "Clave API secreta del dispositivo", example = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6")
        String claveApi
) {
    public String getCodigoEfectivo() {
        return codigo != null && !codigo.isBlank() ? codigo : codigoSensor;
    }
}

