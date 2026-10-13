package com.reservacanchas.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SedeHorarioAtencionTest {

    @Test
    void sede_conCierre23_permiteHorario10a11pm() {
        Sede sede = new Sede();
        sede.setHoraApertura(LocalTime.of(7, 0));
        sede.setHoraCierre(LocalTime.of(23, 0)); // Cierra a las 11:00 PM

        LocalTime inicio = LocalTime.of(22, 0); // 10:00 PM
        LocalTime fin = LocalTime.of(23, 0);    // 11:00 PM

        assertTrue(sede.estaAbiertaEn(inicio), "Las 22:00 deben estar dentro del horario");
        assertTrue(sede.estaAbiertaEn(fin), "Las 23:00 (hora límite de cierre) deben estar dentro del horario");
        assertTrue(inicio.isBefore(sede.getHoraCierre()), "El inicio debe ser anterior a la hora de cierre");
    }

    @Test
    void sede_conCierre23_rechazaHorarioFueraDeAtencion() {
        Sede sede = new Sede();
        sede.setHoraApertura(LocalTime.of(7, 0));
        sede.setHoraCierre(LocalTime.of(23, 0));

        LocalTime antesApertura = LocalTime.of(6, 30);
        LocalTime despuesCierre = LocalTime.of(23, 30);

        assertFalse(sede.estaAbiertaEn(antesApertura));
        assertFalse(sede.estaAbiertaEn(despuesCierre));
    }
}

