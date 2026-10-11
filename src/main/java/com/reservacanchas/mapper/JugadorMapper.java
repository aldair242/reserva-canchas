package com.reservacanchas.mapper;

import com.reservacanchas.dto.JugadorResponse;
import com.reservacanchas.model.Jugador;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface JugadorMapper {

    @Mapping(target = "id", source = "id")
    JugadorResponse toResponse(Jugador jugador);
}
