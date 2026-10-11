package com.reservacanchas.mapper;

import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.CanchaBasquet;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.CanchaPadel;
import com.reservacanchas.model.CanchaVoley;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring")
public interface CanchaMapper {

    @SubclassMapping(source = CanchaFutbol.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaBasquet.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaVoley.class, target = CanchaResponse.class)
    @SubclassMapping(source = CanchaPadel.class, target = CanchaResponse.class)
    CanchaResponse toResponse(Cancha cancha);
}
