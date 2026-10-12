package com.reservacanchas.mapper;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.model.Jugador;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface JugadorMapper {

    @Mapping(target = "id", source = "id")
    JugadorResponse toResponse(Jugador jugador);
}
