package com.reservacanchas.sensor;

import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.SensorArco;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.UbicacionSensor;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispositivoModelTest {

    @Test
    void dispositivo_validarClave_conBCrypt() {
        SensorArco sensor = new SensorArco();
        String clavePlana = "sensor-clave-secreta";
        sensor.setClaveApiHash(BCrypt.hashpw(clavePlana, BCrypt.gensalt()));

        assertTrue(sensor.validarClave(clavePlana));
        assertFalse(sensor.validarClave("otra-clave"));
        assertFalse(sensor.validarClave(null));
    }

    @Test
    void dispositivo_estaEnLinea_calculaConUltimosDosMinutos() {
        SensorArco sensor = new SensorArco();

        // Sin latido -> offline
        assertFalse(sensor.estaEnLinea());

        // Latido hace 1 minuto -> online
        sensor.setUltimoLatido(LocalDateTime.now().minusMinutes(1));
        assertTrue(sensor.estaEnLinea());

        // Latido hace 3 minutos -> offline
        sensor.setUltimoLatido(LocalDateTime.now().minusMinutes(3));
        assertFalse(sensor.estaEnLinea());

        // Al registrar latido actual -> online
        sensor.registrarLatido();
        assertTrue(sensor.estaEnLinea());
    }

    @Test
    void sensorArco_deducirEquipoAnotador() {
        SensorArco sensorArcoA = new SensorArco();
        sensorArcoA.setUbicacion(UbicacionSensor.ARCO_A);
        assertEquals(LadoEquipo.B, sensorArcoA.deducirEquipoAnotador());

        SensorArco sensorArcoB = new SensorArco();
        sensorArcoB.setUbicacion(UbicacionSensor.ARCO_B);
        assertEquals(LadoEquipo.A, sensorArcoB.deducirEquipoAnotador());
    }

    @Test
    void camaraCancha_iniciarGrabacion_creaGrabacionEnEstadoGrabando() {
        CamaraCancha camara = new CamaraCancha();
        camara.setId(5L);
        camara.setCodigo("CAM-01");
        camara.setUrlStream("rtsp://cancha1/stream");

        Partido partido = new Partido();
        partido.setId(10L);

        Grabacion grabacion = camara.iniciarGrabacion(partido);

        assertNotNull(grabacion);
        assertEquals(EstadoGrabacion.GRABANDO, grabacion.getEstado());
        assertEquals(camara, grabacion.getCamara());
        assertEquals(partido, grabacion.getPartido());
        assertEquals("rtsp://cancha1/stream", grabacion.getUrlBase());
        assertNotNull(grabacion.getInicio());
        assertTrue(grabacion.contieneInstante(LocalDateTime.now()));

        // Al finalizar
        grabacion.finalizar();
        assertEquals(EstadoGrabacion.FINALIZADA, grabacion.getEstado());
        assertNotNull(grabacion.getFin());
    }
}

