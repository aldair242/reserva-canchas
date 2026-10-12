package com.reservacanchas.mapper;

import com.reservacanchas.dto.UsuarioResponse;
import com.reservacanchas.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);
}
