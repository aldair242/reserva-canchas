package com.reservacanchas.dto.partido;

import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.model.enums.TipoEvento;

import java.time.LocalDateTime;

public record EventoResponse(
        Long id,
        TipoEvento tipo,
        LadoEquipo equipo,
        LocalDateTime instante,
        int minuto,
        OrigenEvento origen,
        EstadoEvento estado,
        String descripcion,
        boolean afectaMarcador,
        Long anotadorId,
        String anotadorNombre,
        Long asistenteId,
        String asistenteNombre,
        String motivo
) {
}
