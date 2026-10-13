package com.reservacanchas.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.reserva.ReservaRequest;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import com.reservacanchas.repository.CanchaRepository;
import com.reservacanchas.repository.JugadorRepository;
import com.reservacanchas.repository.SedeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ReservaSolapamientoIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("reservacanchas_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.clean-disabled", () -> false);
        registry.add("jwt.secret", () -> "test-secret-key-minimum-256-bits-for-hs256-algorithm-0123456789");
        registry.add("jwt.expiration", () -> 86400000);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SedeRepository sedeRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private JugadorRepository jugadorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.reservacanchas.repository.ReservaRepository reservaRepository;

    @Test
    void buscarPorSedeYFecha_funcionaConFechaYConFechaNula() {
        com.reservacanchas.model.Cancha cancha = canchaRepository.findById(canchaId).orElseThrow();
        Long sedeId = cancha.getSede().getId();

        java.util.List<com.reservacanchas.model.Reserva> conFecha =
                reservaRepository.buscarPorSedeYFecha(sedeId, LocalDate.now());
        org.junit.jupiter.api.Assertions.assertNotNull(conFecha);

        java.util.List<com.reservacanchas.model.Reserva> sinFecha =
                reservaRepository.buscarPorSedeYFecha(sedeId, null);
        org.junit.jupiter.api.Assertions.assertNotNull(sinFecha);
    }

    private Long canchaId;
    private String token;

    @BeforeEach
    void setup() throws Exception {
        Sede sede = new Sede();
        sede.setNombre("Sede Test " + UUID.randomUUID().toString().substring(0, 8));
        sede.setDireccion("Calle Test 123");
        sede.setDistrito("Distrito Test");
        sede.setHoraApertura(LocalTime.of(8, 0));
        sede.setHoraCierre(LocalTime.of(23, 0));
        sede.setActiva(true);
        sede = sedeRepository.save(sede);

        CanchaFutbol cancha = new CanchaFutbol();
        cancha.setNombre("Cancha Test");
        cancha.setSuperficie(TipoSuperficie.CESPED_SINTETICO);
        cancha.setTechada(false);
        cancha.setIluminacion(true);
        cancha.setActiva(true);
        cancha.setSede(sede);
        cancha.setFormato(FormatoFutbol.FUTBOL_7);
        cancha.setTipoTarifa(TipoTarifa.ESTANDAR);
        cancha.setPrecioHora(new BigDecimal("80.00"));
        cancha = canchaRepository.save(cancha);
        canchaId = cancha.getId();

        String correo = "jugador." + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        Jugador jugador = new Jugador();
        jugador.setNombre("Jugador");
        jugador.setApellido("Test");
        jugador.setTelefono("999999999");
        jugador.setCorreo(correo);
        jugador.setPasswordHash(passwordEncoder.encode("password123"));
        jugador.setFechaRegistro(LocalDateTime.now());
        jugador.setActivo(true);
        jugadorRepository.save(jugador);

        LoginRequest login = new LoginRequest(correo, "password123");
        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();
        token = objectMapper.readTree(loginResult.getResponse().getContentAsString())
                .get("token").asText();
    }

    @RepeatedTest(20)
    void dosReservasSimultaneas_mismoHorario_soloUnaSeCrea() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(1);
        LocalTime inicio = LocalTime.of(10, 0);
        LocalTime fin = LocalTime.of(12, 0);

        ReservaRequest request = new ReservaRequest(canchaId, fecha, inicio, fin);
        String body = objectMapper.writeValueAsString(request);

        int numHilos = 2;
        CountDownLatch listos = new CountDownLatch(numHilos);
        CountDownLatch comenzar = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);

        AtomicInteger creadas = new AtomicInteger(0);
        AtomicInteger conflictos = new AtomicInteger(0);

        for (int i = 0; i < numHilos; i++) {
            executor.submit(() -> {
                try {
                    listos.countDown();
                    comenzar.await();
                    MvcResult result = mockMvc.perform(post("/api/reservas")
                                    .header("Authorization", "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andReturn();
                    int status = result.getResponse().getStatus();
                    if (status == 201) {
                        creadas.incrementAndGet();
                    } else if (status == 409) {
                        conflictos.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Ignorar excepciones de hilos
                }
            });
        }

        listos.await(5, TimeUnit.SECONDS);
        comenzar.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(1, creadas.get(), "Debe crearse exactamente una reserva");
        assertEquals(1, conflictos.get(), "La otra reserva debe ser rechazada con 409");
    }

    @Test
    void cincoReservasSimultaneas_mismoHorario_soloUnaSeCrea() throws Exception {
        LocalDate fecha = LocalDate.now().plusDays(2);
        LocalTime inicio = LocalTime.of(14, 0);
        LocalTime fin = LocalTime.of(16, 0);

        ReservaRequest request = new ReservaRequest(canchaId, fecha, inicio, fin);
        String body = objectMapper.writeValueAsString(request);

        int numHilos = 5;
        CountDownLatch listos = new CountDownLatch(numHilos);
        CountDownLatch comenzar = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(numHilos);

        AtomicInteger creadas = new AtomicInteger(0);
        AtomicInteger conflictos = new AtomicInteger(0);

        for (int i = 0; i < numHilos; i++) {
            executor.submit(() -> {
                try {
                    listos.countDown();
                    comenzar.await();
                    MvcResult result = mockMvc.perform(post("/api/reservas")
                                    .header("Authorization", "Bearer " + token)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(body))
                            .andReturn();
                    int status = result.getResponse().getStatus();
                    if (status == 201) {
                        creadas.incrementAndGet();
                    } else if (status == 409) {
                        conflictos.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Ignorar excepciones de hilos
                }
            });
        }

        listos.await(5, TimeUnit.SECONDS);
        comenzar.countDown();
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        assertEquals(1, creadas.get(), "Debe crearse exactamente una reserva");
        assertEquals(4, conflictos.get(), "Las otras 4 reservas deben ser rechazadas con 409");
    }
}
