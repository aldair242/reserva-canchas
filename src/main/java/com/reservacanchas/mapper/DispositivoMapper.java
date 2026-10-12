package com.reservacanchas.mapper;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Dispositivo;
import com.reservacanchas.model.SensorArco;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface DispositivoMapper {

    default DispositivoResponse toResponse(Dispositivo d) {
        if (d == null) {
            return null;
        }
        if (d instanceof SensorArco s) {
            return new DispositivoResponse(
                    s.getId(),
                    s.getTipo(),
                    s.getCodigo(),
                    s.getUltimoLatido(),
                    s.isActivo(),
                    s.estaEnLinea(),
                    s.getCancha() != null ? s.getCancha().getId() : null,
                    s.getCancha() != null ? s.getCancha().getNombre() : null,
                    s.getUbicacion(),
                    s.getSensibilidad(),
                    null,
                    null,
                    null
            );
        } else if (d instanceof CamaraCancha c) {
            return new DispositivoResponse(
                    c.getId(),
                    c.getTipo(),
                    c.getCodigo(),
                    c.getUltimoLatido(),
                    c.isActivo(),
                    c.estaEnLinea(),
                    c.getCancha() != null ? c.getCancha().getId() : null,
                    c.getCancha() != null ? c.getCancha().getNombre() : null,
                    null,
                    null,
                    c.getUrlStream(),
                    c.getResolucion(),
                    c.getFps()
            );
        }
        return new DispositivoResponse(
                d.getId(),
                d.getTipo(),
                d.getCodigo(),
                d.getUltimoLatido(),
                d.isActivo(),
                d.estaEnLinea(),
                d.getCancha() != null ? d.getCancha().getId() : null,
                d.getCancha() != null ? d.getCancha().getNombre() : null,
                null,
                null,
                null,
                null,
                null
        );
    }

    default DispositivoRegistroResponse toRegistroResponse(Dispositivo d, String claveApi) {
        if (d == null) {
            return null;
        }
        if (d instanceof SensorArco s) {
            return new DispositivoRegistroResponse(
                    s.getId(),
                    s.getTipo(),
                    s.getCodigo(),
                    claveApi,
                    s.isActivo(),
                    s.getCancha() != null ? s.getCancha().getId() : null,
                    s.getUbicacion(),
                    s.getSensibilidad(),
                    null,
                    null,
                    null
            );
        } else if (d instanceof CamaraCancha c) {
            return new DispositivoRegistroResponse(
                    c.getId(),
                    c.getTipo(),
                    c.getCodigo(),
                    claveApi,
                    c.isActivo(),
                    c.getCancha() != null ? c.getCancha().getId() : null,
                    null,
                    null,
                    c.getUrlStream(),
                    c.getResolucion(),
                    c.getFps()
            );
        }
        return new DispositivoRegistroResponse(
                d.getId(),
                d.getTipo(),
                d.getCodigo(),
                claveApi,
                d.isActivo(),
                d.getCancha() != null ? d.getCancha().getId() : null,
                null,
                null,
                null,
                null,
                null
        );
    }
}

