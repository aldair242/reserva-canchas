package com.reservacanchas.mapper;

import com.reservacanchas.dto.reserva.ReservaResponse;
import com.reservacanchas.model.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ReservaMapper {

    @Mapping(target = "organizadorId", source = "organizador.id")
    @Mapping(target = "organizadorNombre", source = "organizador.nombreVisible")
    @Mapping(target = "canchaId", source = "cancha.id")
    @Mapping(target = "canchaNombre", source = "cancha.nombre")
    @Mapping(target = "pagada", expression = "java(reserva.estaPagada())")
    @Mapping(target = "partidoId", source = "partido.id")
    ReservaResponse toResponse(Reserva reserva);
}
