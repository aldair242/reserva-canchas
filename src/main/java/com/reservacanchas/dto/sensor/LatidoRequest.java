package com.reservacanchas.dto.sensor;

public record LatidoRequest(
        String codigo,
        String codigoSensor,
        String claveApi
) {
    public String getCodigoEfectivo() {
        return codigo != null && !codigo.isBlank() ? codigo : codigoSensor;
    }
}

