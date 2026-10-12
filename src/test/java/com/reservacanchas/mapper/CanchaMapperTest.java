package com.reservacanchas.mapper;

import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.model.CanchaBasquet;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.CanchaPadel;
import com.reservacanchas.model.CanchaVoley;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CanchaMapperTest {

    private CanchaMapper canchaMapper;
    private Sede sede;

    @BeforeEach
    void setUp() {
        canchaMapper = new CanchaMapperImpl();
        sede = new Sede();
        sede.setId(10L);
        sede.setNombre("Sede Principal San Isidro");
    }

    @Test
    void toResponse_canchaFutbol_mapeaTodosLosCamposYSede() {
        CanchaFutbol cf = new CanchaFutbol();
        cf.setId(1L);
        cf.setNombre("Cancha Sintetica 7");
        cf.setSuperficie(TipoSuperficie.CESPED_SINTETICO);
        cf.setTechada(true);
        cf.setIluminacion(true);
        cf.setActiva(true);
        cf.setSede(sede);
        cf.setFormato(FormatoFutbol.FUTBOL_7);
        cf.setTipoTarifa(TipoTarifa.ESTANDAR);
        cf.setPrecioHora(BigDecimal.valueOf(120.00));

        CanchaResponse res = canchaMapper.toResponse(cf);

        assertNotNull(res);
        assertEquals(1L, res.id());
        assertEquals("Cancha Sintetica 7", res.nombre());
        assertEquals(TipoSuperficie.CESPED_SINTETICO, res.superficie());
        assertTrue(res.techada());
        assertTrue(res.iluminacion());
        assertTrue(res.activa());
        assertEquals(10L, res.sedeId());
        assertEquals("Sede Principal San Isidro", res.sedeNombre());
        assertEquals(TipoDeporte.FUTBOL, res.deporte());
        assertEquals(14, res.capacidadJugadores());
        assertEquals(FormatoFutbol.FUTBOL_7, res.formato());
        assertNull(res.medioCampo());
        assertNull(res.alturaRed());
        assertNull(res.esDePlaya());
        assertNull(res.paredesDeVidrio());
        assertEquals(TipoTarifa.ESTANDAR, res.tipoTarifa());
        assertEquals(BigDecimal.valueOf(120.00), res.precioHora());
    }

    @Test
    void toResponse_canchaBasquet_mapeaMedioCampo() {
        CanchaBasquet cb = new CanchaBasquet();
        cb.setId(2L);
        cb.setNombre("Cancha Basquet 3x3");
        cb.setSuperficie(TipoSuperficie.PARQUET);
        cb.setSede(sede);
        cb.setMedioCampo(true);

        CanchaResponse res = canchaMapper.toResponse(cb);

        assertNotNull(res);
        assertEquals(2L, res.id());
        assertEquals(10L, res.sedeId());
        assertEquals("Sede Principal San Isidro", res.sedeNombre());
        assertEquals(TipoDeporte.BASQUET, res.deporte());
        assertEquals(6, res.capacidadJugadores());
        assertTrue(res.medioCampo());
        assertNull(res.formato());
        assertNull(res.alturaRed());
        assertNull(res.esDePlaya());
        assertNull(res.paredesDeVidrio());
    }

    @Test
    void toResponse_canchaVoley_mapeaAlturaRedYEsDePlaya() {
        CanchaVoley cv = new CanchaVoley();
        cv.setId(3L);
        cv.setNombre("Cancha Voley Playa");
        cv.setSuperficie(TipoSuperficie.LOSA);
        cv.setSede(sede);
        cv.setAlturaRed(2.43);
        cv.setEsDePlaya(true);

        CanchaResponse res = canchaMapper.toResponse(cv);

        assertNotNull(res);
        assertEquals(3L, res.id());
        assertEquals(10L, res.sedeId());
        assertEquals(TipoDeporte.VOLEY, res.deporte());
        assertEquals(4, res.capacidadJugadores());
        assertEquals(2.43, res.alturaRed());
        assertTrue(res.esDePlaya());
        assertNull(res.formato());
        assertNull(res.medioCampo());
        assertNull(res.paredesDeVidrio());
    }

    @Test
    void toResponse_canchaPadel_mapeaParedesDeVidrio() {
        CanchaPadel cp = new CanchaPadel();
        cp.setId(4L);
        cp.setNombre("Cancha Padel Cristal");
        cp.setSuperficie(TipoSuperficie.CESPED_SINTETICO);
        cp.setSede(sede);
        cp.setParedesDeVidrio(true);

        CanchaResponse res = canchaMapper.toResponse(cp);

        assertNotNull(res);
        assertEquals(4L, res.id());
        assertEquals(10L, res.sedeId());
        assertEquals(TipoDeporte.PADEL, res.deporte());
        assertEquals(4, res.capacidadJugadores());
        assertTrue(res.paredesDeVidrio());
        assertNull(res.formato());
        assertNull(res.medioCampo());
        assertNull(res.alturaRed());
        assertNull(res.esDePlaya());
    }
}
