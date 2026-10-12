package com.reservacanchas.mapper;

import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.CanchaBasquet;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.CanchaPadel;
import com.reservacanchas.model.CanchaVoley;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface CanchaMapper {

    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "medioCampo", ignore = true)
    @Mapping(target = "alturaRed", ignore = true)
    @Mapping(target = "esDePlaya", ignore = true)
    @Mapping(target = "paredesDeVidrio", ignore = true)
    @SubclassMapping(source = CanchaFutbol.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaBasquet.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaVoley.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaPadel.class, target = CanchaResponse.class)
    CanchaResponse toResponse(Cancha cancha);

    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    @Mapping(target = "medioCampo", ignore = true)
    @Mapping(target = "alturaRed", ignore = true)
    @Mapping(target = "esDePlaya", ignore = true)
    @Mapping(target = "paredesDeVidrio", ignore = true)
    CanchaResponse toResponse(CanchaFutbol cancha);

    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "alturaRed", ignore = true)
    @Mapping(target = "esDePlaya", ignore = true)
    @Mapping(target = "paredesDeVidrio", ignore = true)
    CanchaResponse toResponse(CanchaBasquet cancha);

    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "medioCampo", ignore = true)
    @Mapping(target = "paredesDeVidrio", ignore = true)
    CanchaResponse toResponse(CanchaVoley cancha);

    @Mapping(target = "sedeId", source = "sede.id")
    @Mapping(target = "sedeNombre", source = "sede.nombre")
    @Mapping(target = "formato", ignore = true)
    @Mapping(target = "medioCampo", ignore = true)
    @Mapping(target = "alturaRed", ignore = true)
    @Mapping(target = "esDePlaya", ignore = true)
    CanchaResponse toResponse(CanchaPadel cancha);
}
