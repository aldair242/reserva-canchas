package com.reservacanchas.mapper;

import com.reservacanchas.dto.sede.SedeRequest;
import com.reservacanchas.dto.sede.SedeResponse;
import com.reservacanchas.model.Sede;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SedeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "administradorSede", ignore = true)
    @Mapping(target = "canchas", ignore = true)
    Sede toEntity(SedeRequest request);

    @Mapping(target = "administradorSedeId", source = "administradorSede.id")
    SedeResponse toResponse(Sede sede);
}
