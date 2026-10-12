package com.reservacanchas.sensor;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.sensor.EventoSensorRequest;
import com.reservacanchas.dto.sensor.LatidoRequest;
import com.reservacanchas.dto.sensor.LatidoResponse;
import com.reservacanchas.exception.CredencialesInvalidasException;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.mapper.EventoMapper;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.SensorArco;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.model.enums.TipoEvento;
import com.reservacanchas.model.enums.UbicacionSensor;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.DispositivoRepository;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.SensorArcoRepository;
import com.reservacanchas.service.ClipService;
import com.reservacanchas.service.impl.SensorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorArcoRepository sensorArcoRepository;

    @Mock
    private DispositivoRepository dispositivoRepository;

    @Mock
    private PartidoRepository partidoRepository;

    @Mock
    private EventoPartidoRepository eventoRepository;

    @Mock
    private GrabacionRepository grabacionRepository;

    @Mock
    private CamaraCanchaRepository camaraCanchaRepository;

    @Mock
    private ClipService clipService;

    @Mock
    private EventoMapper eventoMapper;

    @InjectMocks
    private SensorServiceImpl sensorService;

    private SensorArco sensor;
    private CanchaFutbol cancha;
    private Partido partido;
    private final String codigoValido = "SENSOR-01";
    private final String clavePlanaValida = "secret123";

    @BeforeEach
    void setUp() {
        cancha = new CanchaFutbol();
        cancha.setId(10L);
        cancha.setNombre("Cancha 1");

        sensor = new SensorArco();
        sensor.setId(1L);
        sensor.setCodigo(codigoValido);
        sensor.setClaveApiHash(BCrypt.hashpw(clavePlanaValida, BCrypt.gensalt()));
        sensor.setActivo(true);
        sensor.setCancha(cancha);
        sensor.setUbicacion(UbicacionSensor.ARCO_A);

        partido = new Partido();
        partido.setId(100L);
        partido.setEstado(EstadoPartido.EN_JUEGO);
        partido.setInicioReal(LocalDateTime.now().minusMinutes(20));
    }

    @Test
    void procesarEvento_sensorNoExiste_lanzaCredencialesInvalidasException() {
        when(sensorArcoRepository.findByCodigo("INEXISTENTE")).thenReturn(Optional.empty());

        EventoSensorRequest request = new EventoSensorRequest("INEXISTENTE", clavePlanaValida, LocalDateTime.now());

        assertThrows(CredencialesInvalidasException.class, () -> sensorService.procesarEvento(request));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void procesarEvento_claveIncorrecta_lanzaCredencialesInvalidasException() {
        when(sensorArcoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));

        EventoSensorRequest request = new EventoSensorRequest(codigoValido, "clave-incorrecta", LocalDateTime.now());

        assertThrows(CredencialesInvalidasException.class, () -> sensorService.procesarEvento(request));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void procesarEvento_sensorInactivo_lanzaCredencialesInvalidasException() {
        sensor.setActivo(false);
        when(sensorArcoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));

        EventoSensorRequest request = new EventoSensorRequest(codigoValido, clavePlanaValida, LocalDateTime.now());

        assertThrows(CredencialesInvalidasException.class, () -> sensorService.procesarEvento(request));
        verify(eventoRepository, never()).save(any());
    }

    @Test
    void procesarEvento_sinPartidoEnJuego_lanzaOperacionNoPermitidaYNoCreaNada() {
        when(sensorArcoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));
        when(partidoRepository.findByCanchaIdAndEstado(cancha.getId(), EstadoPartido.EN_JUEGO))
                .thenReturn(Optional.empty());

        EventoSensorRequest request = new EventoSensorRequest(codigoValido, clavePlanaValida, LocalDateTime.now());

        OperacionNoPermitidaException ex = assertThrows(
                OperacionNoPermitidaException.class,
                () -> sensorService.procesarEvento(request)
        );
        assertTrue(ex.getMessage().contains("No hay ningun partido en juego"));

        // Se verifica que no se creo ningun gol ni clip
        verify(eventoRepository, never()).save(any());
        verify(clipService, never()).generarClipAsync(any(), any(), any(), any(int.class), any(int.class));
    }

    @Test
    void procesarEvento_conPartidoEnJuego_creaGolPendienteYDisparaClipAsync() {
        when(sensorArcoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));
        when(partidoRepository.findByCanchaIdAndEstado(cancha.getId(), EstadoPartido.EN_JUEGO))
                .thenReturn(Optional.of(partido));

        Grabacion grabacion = new Grabacion();
        grabacion.setId(50L);
        grabacion.setPartido(partido);
        grabacion.setEstado(EstadoGrabacion.GRABANDO);
        when(grabacionRepository.findFirstByPartidoIdAndEstado(partido.getId(), EstadoGrabacion.GRABANDO))
                .thenReturn(Optional.of(grabacion));

        LocalDateTime instanteGol = LocalDateTime.now();
        EventoSensorRequest request = new EventoSensorRequest(codigoValido, clavePlanaValida, instanteGol);

        when(eventoRepository.save(any(Gol.class))).thenAnswer(invocation -> {
            Gol g = invocation.getArgument(0);
            g.setId(200L);
            return g;
        });

        EventoResponse responseEsperado = new EventoResponse(
                200L, TipoEvento.GOL, LadoEquipo.B, instanteGol, 20, OrigenEvento.SENSOR,
                EstadoEvento.PENDIENTE_ATRIBUCION, "Gol del equipo B", true,
                null, null, null, null, null
        );
        when(eventoMapper.toResponse(any(Gol.class))).thenReturn(responseEsperado);

        EventoResponse response = sensorService.procesarEvento(request);

        assertNotNull(response);
        assertEquals(LadoEquipo.B, response.equipo());
        assertEquals(EstadoEvento.PENDIENTE_ATRIBUCION, response.estado());

        ArgumentCaptor<Gol> golCaptor = ArgumentCaptor.forClass(Gol.class);
        verify(eventoRepository).save(golCaptor.capture());
        Gol golGuardado = golCaptor.getValue();

        assertEquals(EstadoEvento.PENDIENTE_ATRIBUCION, golGuardado.getEstado());
        assertEquals(OrigenEvento.SENSOR, golGuardado.getOrigen());
        // Como el sensor está en ARCO_A, el gol corresponde al equipo B
        assertEquals(LadoEquipo.B, golGuardado.getEquipo());
        assertEquals(partido, golGuardado.getPartido());

        // Se verifica que se disparo la generacion asincrona del clip
        verify(clipService).generarClipAsync(eq(grabacion), eq(golGuardado), eq(instanteGol), any(int.class), any(int.class));
    }

    @Test
    void registrarLatido_conCredencialesValidas_actualizaUltimoLatido() {
        when(dispositivoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));
        when(dispositivoRepository.save(any(SensorArco.class))).thenAnswer(i -> i.getArgument(0));

        LatidoRequest request = new LatidoRequest(codigoValido, null, clavePlanaValida);
        LatidoResponse response = sensorService.registrarLatido(request);

        assertNotNull(response);
        assertEquals(codigoValido, response.codigo());
        assertTrue(response.enLinea());
        assertNotNull(sensor.getUltimoLatido());
    }

    @Test
    void registrarLatido_codigoInexistente_lanzaCredencialesInvalidas() {
        when(dispositivoRepository.findByCodigo("INEXISTENTE")).thenReturn(Optional.empty());

        LatidoRequest request = new LatidoRequest("INEXISTENTE", null, clavePlanaValida);
        CredencialesInvalidasException ex = assertThrows(
                CredencialesInvalidasException.class,
                () -> sensorService.registrarLatido(request)
        );
        assertEquals("Credenciales invalidas del sensor", ex.getMessage());
    }

    @Test
    void registrarLatido_claveIncorrecta_lanzaCredencialesInvalidas() {
        when(dispositivoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));

        LatidoRequest request = new LatidoRequest(codigoValido, null, "clave-incorrecta");
        CredencialesInvalidasException ex = assertThrows(
                CredencialesInvalidasException.class,
                () -> sensorService.registrarLatido(request)
        );
        assertEquals("Credenciales invalidas del sensor", ex.getMessage());
    }

    @Test
    void registrarLatido_sensorInactivo_lanzaCredencialesInvalidas() {
        sensor.setActivo(false);
        when(dispositivoRepository.findByCodigo(codigoValido)).thenReturn(Optional.of(sensor));

        LatidoRequest request = new LatidoRequest(codigoValido, null, clavePlanaValida);
        CredencialesInvalidasException ex = assertThrows(
                CredencialesInvalidasException.class,
                () -> sensorService.registrarLatido(request)
        );
        assertEquals("Credenciales invalidas del sensor", ex.getMessage());
    }
}
