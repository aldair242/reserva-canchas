package com.reservacanchas.mapper;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.partido.ParticipanteResponse;
import com.reservacanchas.dto.partido.PartidoResponse;
import com.reservacanchas.model.EventoPartido;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.GolEnContra;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.TarjetaAmarilla;
import com.reservacanchas.model.TarjetaRoja;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.SubclassMapping;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PartidoMapper {

    @Mapping(target = "reservaId", source = "reserva.id")
    PartidoResponse toResponse(Partido partido);

    @Mapping(target = "personaId", source = "persona.id")
    @Mapping(target = "nombreVisible", source = "nombreVisible")
    @Mapping(target = "invitado", expression = "java(participante.esInvitado())")
    ParticipanteResponse toParticipanteResponse(ParticipantePartido participante);

    @SubclassMapping(source = Gol.class, target = EventoResponse.class)
    @SubclassMapping(source = GolEnContra.class, target = EventoResponse.class)
    @SubclassMapping(source = TarjetaAmarilla.class, target = EventoResponse.class)
    @SubclassMapping(source = TarjetaRoja.class, target = EventoResponse.class)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "descripcion", expression = "java(evento.descripcion())")
    @Mapping(target = "afectaMarcador", expression = "java(evento.afectaMarcador())")
    @Mapping(target = "anotadorId", ignore = true)
    @Mapping(target = "anotadorNombre", ignore = true)
    @Mapping(target = "asistenteId", ignore = true)
    @Mapping(target = "asistenteNombre", ignore = true)
    @Mapping(target = "motivo", ignore = true)
    EventoResponse toResponse(EventoPartido evento);

    @Mapping(target = "tipo", constant = "GOL")
    @Mapping(target = "descripcion", expression = "java(gol.descripcion())")
    @Mapping(target = "afectaMarcador", expression = "java(gol.afectaMarcador())")
    @Mapping(target = "anotadorId", source = "anotador.id")
    @Mapping(target = "anotadorNombre", source = "anotador.nombreVisible")
    @Mapping(target = "asistenteId", source = "asistente.id")
    @Mapping(target = "asistenteNombre", source = "asistente.nombreVisible")
    @Mapping(target = "motivo", ignore = true)
    EventoResponse toResponse(Gol gol);

    @Mapping(target = "tipo", constant = "GOL_EN_CONTRA")
    @Mapping(target = "descripcion", expression = "java(golEnContra.descripcion())")
    @Mapping(target = "afectaMarcador", expression = "java(golEnContra.afectaMarcador())")
    @Mapping(target = "anotadorId", ignore = true)
    @Mapping(target = "anotadorNombre", ignore = true)
    @Mapping(target = "asistenteId", ignore = true)
    @Mapping(target = "asistenteNombre", ignore = true)
    @Mapping(target = "motivo", ignore = true)
    EventoResponse toResponse(GolEnContra golEnContra);

    @Mapping(target = "tipo", constant = "TARJETA_AMARILLA")
    @Mapping(target = "descripcion", expression = "java(tarjeta.descripcion())")
    @Mapping(target = "afectaMarcador", expression = "java(tarjeta.afectaMarcador())")
    @Mapping(target = "anotadorId", ignore = true)
    @Mapping(target = "anotadorNombre", ignore = true)
    @Mapping(target = "asistenteId", ignore = true)
    @Mapping(target = "asistenteNombre", ignore = true)
    @Mapping(target = "motivo", source = "motivo")
    EventoResponse toResponse(TarjetaAmarilla tarjeta);

    @Mapping(target = "tipo", constant = "TARJETA_ROJA")
    @Mapping(target = "descripcion", expression = "java(tarjeta.descripcion())")
    @Mapping(target = "afectaMarcador", expression = "java(tarjeta.afectaMarcador())")
    @Mapping(target = "anotadorId", ignore = true)
    @Mapping(target = "anotadorNombre", ignore = true)
    @Mapping(target = "asistenteId", ignore = true)
    @Mapping(target = "asistenteNombre", ignore = true)
    @Mapping(target = "motivo", source = "motivo")
    EventoResponse toResponse(TarjetaRoja tarjeta);
}
