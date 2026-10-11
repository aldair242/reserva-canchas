package com.reservacanchas.mapper;

import com.reservacanchas.dto.UsuarioResponse;
import com.reservacanchas.model.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);
}
