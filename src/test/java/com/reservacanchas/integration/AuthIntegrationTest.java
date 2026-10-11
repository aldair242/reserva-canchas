package com.reservacanchas.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:postgresql://localhost:5432/reservacanchas_test");
        registry.add("spring.datasource.username", () -> "reservacanchas");
        registry.add("spring.datasource.password", () -> "reservacanchas");
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.clean-disabled", () -> false);
        registry.add("jwt.secret", () -> "test-secret-key-minimum-256-bits-for-hs256-algorithm-0123456789");
        registry.add("jwt.expiration", () -> 86400000);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registro_login_y_acceso_protegido() throws Exception {
        String correo = "maria." + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        RegistroRequest registro = new RegistroRequest(
                "Maria", "Lopez", "987654321",
                correo, "password123",
                "mari", "http://foto.jpg", "Defensa"
        );

        MvcResult registroResult = mockMvc.perform(post("/api/auth/registro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.jugador.correo").value(correo))
                .andReturn();

        String tokenRegistro = extractToken(registroResult);

        mockMvc.perform(get("/api/jugadores/me")
                        .header("Authorization", "Bearer " + tokenRegistro))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(correo));

        LoginRequest login = new LoginRequest(correo, "password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        String tokenLogin = extractToken(loginResult);

        mockMvc.perform(get("/api/jugadores/me")
                        .header("Authorization", "Bearer " + tokenLogin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value(correo));
    }

    @Test
    void acceso_protegido_sin_token_debe_retornar_401() throws Exception {
        mockMvc.perform(get("/api/jugadores/me"))
                .andExpect(status().isUnauthorized());
    }

    private String extractToken(MvcResult result) throws Exception {
        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        return node.get("token").asText();
    }
}
