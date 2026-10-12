package com.reservacanchas.mapper;

import com.reservacanchas.dto.clip.ClipDetalleResponse;
import com.reservacanchas.dto.clip.ClipResponse;
import com.reservacanchas.model.Clip;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ClipMapper {

    @Mapping(target = "partidoId", source = "partido.id")
    @Mapping(target = "grabacionId", source = "grabacion.id")
    ClipResponse toResponse(Clip clip);

    default ClipDetalleResponse toDetalleResponse(Clip clip, String urlFirmada) {
        if (clip == null) {
            return null;
        }
        return new ClipDetalleResponse(
                clip.getId(),
                clip.getUrlVideo(),
                urlFirmada,
                clip.getUrlMiniatura(),
                clip.getSegundosAntes(),
                clip.getSegundosDespues(),
                clip.getDuracionSegundos(),
                clip.getInstante(),
                clip.getEstado(),
                clip.isVisible(),
                clip.getVistas(),
                clip.getPartido() != null ? clip.getPartido().getId() : null,
                clip.getGrabacion() != null ? clip.getGrabacion().getId() : null
        );
    }
}

