package com.reservacanchas.sensor;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.dto.dispositivo.RegistroCamaraRequest;
import com.reservacanchas.dto.dispositivo.RegistroSensorRequest;
import com.reservacanchas.mapper.DispositivoMapper;
import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.SensorArco;
import com.reservacanchas.model.enums.UbicacionSensor;
import com.reservacanchas.repository.CanchaRepository;
import com.reservacanchas.repository.DispositivoRepository;
import com.reservacanchas.service.SecurityService;
import com.reservacanchas.service.impl.DispositivoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DispositivoServiceTest {

    @Mock
    private DispositivoRepository dispositivoRepository;

    @Mock
    private CanchaRepository canchaRepository;

    @Mock
    private DispositivoMapper dispositivoMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityService securityService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DispositivoServiceImpl dispositivoService;

    private CanchaFutbol cancha;
    private Sede sede;

    @BeforeEach
    void setUp() {
        sede = new Sede();
        sede.setId(1L);

        cancha = new CanchaFutbol();
        cancha.setId(10L);
        cancha.setSede(sede);
    }

    @Test
    void registrarSensor_guardaHashYRetornaClavePlanaUnaSolaVez() {
        when(canchaRepository.findById(10L)).thenReturn(Optional.of(cancha));
        when(dispositivoRepository.existsByCodigo("SENSOR-A1")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed-bcrypt-key");

        when(dispositivoRepository.save(any(SensorArco.class))).thenAnswer(invocation -> {
            SensorArco s = invocation.getArgument(0);
            s.setId(50L);
            return s;
        });

        when(dispositivoMapper.toRegistroResponse(any(SensorArco.class), any())).thenAnswer(invocation -> {
            SensorArco s = invocation.getArgument(0);
            String clavePlana = invocation.getArgument(1);
            return new DispositivoRegistroResponse(
                    s.getId(), s.getTipo(), s.getCodigo(), clavePlana, s.isActivo(), 10L,
                    s.getUbicacion(), s.getSensibilidad(), null, null, null
            );
        });

        RegistroSensorRequest request = new RegistroSensorRequest("SENSOR-A1", UbicacionSensor.ARCO_A, 75);
        DispositivoRegistroResponse response = dispositivoService.registrarSensor(10L, request, authentication);

        assertNotNull(response);
        assertEquals("SENSOR-A1", response.codigo());
        assertNotNull(response.claveApi());
        assertTrue(response.claveApi().startsWith("DEV-"));

        ArgumentCaptor<SensorArco> captor = ArgumentCaptor.forClass(SensorArco.class);
        verify(dispositivoRepository).save(captor.capture());
        SensorArco sensorGuardado = captor.getValue();

        // En la base de datos se guarda el hash y nunca el texto plano
        assertEquals("hashed-bcrypt-key", sensorGuardado.getClaveApiHash());
        assertEquals(UbicacionSensor.ARCO_A, sensorGuardado.getUbicacion());
        assertEquals(75, sensorGuardado.getSensibilidad());
    }

    @Test
    void registrarCamara_guardaCamaraYDevuelveClavePlana() {
        when(canchaRepository.findById(10L)).thenReturn(Optional.of(cancha));
        when(dispositivoRepository.existsByCodigo("CAM-01")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed-cam-key");

        when(dispositivoRepository.save(any(CamaraCancha.class))).thenAnswer(invocation -> {
            CamaraCancha c = invocation.getArgument(0);
            c.setId(60L);
            return c;
        });

        when(dispositivoMapper.toRegistroResponse(any(CamaraCancha.class), any())).thenAnswer(invocation -> {
            CamaraCancha c = invocation.getArgument(0);
            String clavePlana = invocation.getArgument(1);
            return new DispositivoRegistroResponse(
                    c.getId(), c.getTipo(), c.getCodigo(), clavePlana, c.isActivo(), 10L,
                    null, null, c.getUrlStream(), c.getResolucion(), c.getFps()
            );
        });

        RegistroCamaraRequest request = new RegistroCamaraRequest("CAM-01", "rtsp://live", "1080p", 60);
        DispositivoRegistroResponse response = dispositivoService.registrarCamara(10L, request, authentication);

        assertNotNull(response);
        assertEquals("CAM-01", response.codigo());
        assertNotNull(response.claveApi());

        ArgumentCaptor<CamaraCancha> captor = ArgumentCaptor.forClass(CamaraCancha.class);
        verify(dispositivoRepository).save(captor.capture());
        CamaraCancha camaraGuardada = captor.getValue();

        assertEquals("hashed-cam-key", camaraGuardada.getClaveApiHash());
        assertEquals("rtsp://live", camaraGuardada.getUrlStream());
        assertEquals("1080p", camaraGuardada.getResolucion());
        assertEquals(60, camaraGuardada.getFps());
    }

    @Test
    void desactivar_cambiaActivoAFalso() {
        SensorArco sensor = new SensorArco();
        sensor.setId(1L);
        sensor.setCancha(cancha);
        sensor.setActivo(true);

        when(dispositivoRepository.findById(1L)).thenReturn(Optional.of(sensor));
        when(dispositivoRepository.save(sensor)).thenReturn(sensor);

        DispositivoResponse responseEsperado = new DispositivoResponse(
                1L, "SENSOR_ARCO", "SENSOR-01", null, false, false, 10L, "Cancha",
                UbicacionSensor.ARCO_A, 50, null, null, null
        );
        when(dispositivoMapper.toResponse(sensor)).thenReturn(responseEsperado);

        DispositivoResponse response = dispositivoService.desactivar(1L, authentication);

        assertNotNull(response);
        assertFalse(sensor.isActivo());
        verify(dispositivoRepository).save(sensor);
    }
}

