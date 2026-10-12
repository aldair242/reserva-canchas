package com.reservacanchas.partido;

import com.reservacanchas.model.Gol;
import com.reservacanchas.model.ParticipantePartido;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.service.impl.EventoCierreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventoCierreServiceTest {

    @Mock
    private EventoPartidoRepository eventoRepository;

    @InjectMocks
    private EventoCierreService eventoCierreService;

    @Test
    void cerrarEventosVencidos_golesSinAtribucionQuedanComoGolDeEquipo() {
        Gol golVencido = new Gol();
        golVencido.setId(1L);
        golVencido.setEstado(EstadoEvento.PENDIENTE_ATRIBUCION);
        golVencido.setFechaLimiteAtribucion(LocalDateTime.now().minusHours(1));
        ParticipantePartido anotador = new ParticipantePartido();
        anotador.setId(10L);
        golVencido.setAnotador(anotador);

        when(eventoRepository.findGolesVencidos(eq(EstadoEvento.PENDIENTE_ATRIBUCION), any()))
                .thenReturn(List.of(golVencido));

        eventoCierreService.cerrarEventosVencidos();

        ArgumentCaptor<Gol> captor = ArgumentCaptor.forClass(Gol.class);
        verify(eventoRepository, times(1)).save(captor.capture());
        Gol guardado = captor.getValue();

        assertEquals(EstadoEvento.CONFIRMADO, guardado.getEstado());
        assertNull(guardado.getAnotador());
        assertNull(guardado.getAsistente());
    }

    @Test
    void cerrarEventosVencidos_sinEventosVencidos_noGuardaNada() {
        when(eventoRepository.findGolesVencidos(eq(EstadoEvento.PENDIENTE_ATRIBUCION), any()))
                .thenReturn(List.of());

        eventoCierreService.cerrarEventosVencidos();

        verify(eventoRepository, times(0)).save(any());
    }
}
