package com.reservacanchas.sensor;

import com.reservacanchas.controller.ClipController;
import com.reservacanchas.service.ClipService;
import com.reservacanchas.service.impl.AlmacenamientoLocal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClipControllerSecurityTest {

    private MockMvc mockMvc;

    @Mock
    private ClipService clipService;

    private AlmacenamientoLocal almacenamientoLocal;

    @TempDir
    Path tempDir;

    private String nombreArchivoReal;

    @BeforeEach
    void setUp() throws Exception {
        almacenamientoLocal = new AlmacenamientoLocal(tempDir.toString());

        File dummyFile = tempDir.resolve("sample.mp4").toFile();
        Files.writeString(dummyFile.toPath(), "video-content-bytes");
        nombreArchivoReal = almacenamientoLocal.subir(dummyFile);

        ClipController clipController = new ClipController(clipService, almacenamientoLocal);
        mockMvc = MockMvcBuilders.standaloneSetup(clipController).build();
    }

    @Test
    void servirArchivo_urlFirmadaValida_retorna200() throws Exception {
        String urlFirmada = almacenamientoLocal.generarUrlFirmada(nombreArchivoReal, 300);

        mockMvc.perform(get(urlFirmada))
                .andExpect(status().isOk());
    }

    @Test
    void servirArchivo_urlVencida_retorna403() throws Exception {
        // Expirada hace 10 segundos
        String urlVencida = almacenamientoLocal.generarUrlFirmada(nombreArchivoReal, -10);

        mockMvc.perform(get(urlVencida))
                .andExpect(status().isForbidden());
    }

    @Test
    void servirArchivo_firmaAlterada_retorna403() throws Exception {
        String urlValida = almacenamientoLocal.generarUrlFirmada(nombreArchivoReal, 300);
        String urlAlterada = urlValida.substring(0, urlValida.length() - 4) + "abcd";

        mockMvc.perform(get(urlAlterada))
                .andExpect(status().isForbidden());
    }

    @Test
    void servirArchivo_sinFirmaNiExpiracion_retorna403() throws Exception {
        mockMvc.perform(get("/api/clips/archivos/" + nombreArchivoReal))
                .andExpect(status().isForbidden());
    }
}

