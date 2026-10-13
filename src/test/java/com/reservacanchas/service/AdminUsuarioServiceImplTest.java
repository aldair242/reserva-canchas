package com.reservacanchas.service;

import com.reservacanchas.dto.admin.AdministradorSedeResponse;
import com.reservacanchas.dto.admin.CrearAdminSedeRequest;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.repository.AdministradorSedeRepository;
import com.reservacanchas.repository.UsuarioRepository;
import com.reservacanchas.service.impl.AdminUsuarioServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioServiceImplTest {

    @Mock
    private AdministradorSedeRepository administradorSedeRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private com.reservacanchas.repository.SedeRepository sedeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AdminUsuarioServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new AdminUsuarioServiceImpl(
                administradorSedeRepository,
                usuarioRepository,
                sedeRepository,
                passwordEncoder
        );
    }

    @Test
    void crearAdministradorSede_exitoso() {
        CrearAdminSedeRequest request = new CrearAdminSedeRequest(
                "Carlos", "Gómez", "carlos@sede.com", "pass123", "987654321"
        );

        when(usuarioRepository.existsByCorreo("carlos@sede.com")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hash123");
        when(administradorSedeRepository.save(any(AdministradorSede.class))).thenAnswer(inv -> {
            AdministradorSede saved = inv.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        AdministradorSedeResponse response = service.crearAdministradorSede(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Carlos", response.nombre());
        assertEquals("carlos@sede.com", response.correo());
        assertTrue(response.activo());
        verify(administradorSedeRepository).save(any(AdministradorSede.class));
    }

    @Test
    void crearAdministradorSede_correoDuplicado_lanzaExcepcion() {
        CrearAdminSedeRequest request = new CrearAdminSedeRequest(
                "Carlos", "Gómez", "existente@sede.com", "pass123", "987654321"
        );

        when(usuarioRepository.existsByCorreo("existente@sede.com")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                service.crearAdministradorSede(request)
        );

        assertTrue(ex.getMessage().contains("Ya existe un usuario"));
        verify(administradorSedeRepository, never()).save(any());
    }

    @Test
    void listarAdministradoresSede_retornaLista() {
        AdministradorSede admin = new AdministradorSede();
        admin.setId(1L);
        admin.setNombre("Ana");
        admin.setApellido("Lopez");
        admin.setCorreo("ana@sede.com");
        admin.setTelefono("911222333");
        admin.setActivo(true);

        when(administradorSedeRepository.findAll()).thenReturn(List.of(admin));

        List<AdministradorSedeResponse> resultado = service.listarAdministradoresSede();

        assertEquals(1, resultado.size());
        assertEquals("Ana", resultado.get(0).nombre());
    }

    @Test
    void actualizarAdministradorSede_exitoso() {
        Long id = 5L;
        AdministradorSede existente = new AdministradorSede();
        existente.setId(id);
        existente.setNombre("Carlos");
        existente.setApellido("Gómez");
        existente.setCorreo("carlos@sede.com");
        existente.setTelefono("987654321");
        existente.setActivo(true);

        when(administradorSedeRepository.findById(id)).thenReturn(java.util.Optional.of(existente));
        when(administradorSedeRepository.save(any(AdministradorSede.class))).thenAnswer(inv -> inv.getArgument(0));

        com.reservacanchas.dto.admin.ActualizarAdminSedeRequest request =
                new com.reservacanchas.dto.admin.ActualizarAdminSedeRequest(
                        "Carlos Alberto", "Gómez", "carlos@sede.com", null, "999888777", true
                );

        AdministradorSedeResponse res = service.actualizarAdministradorSede(id, request);

        assertEquals("Carlos Alberto", res.nombre());
        assertEquals("999888777", res.telefono());
        verify(administradorSedeRepository).save(existente);
    }

    @Test
    void eliminarAdministradorSede_desvinculaSedesYBorra() {
        Long id = 7L;
        AdministradorSede existente = new AdministradorSede();
        existente.setId(id);

        com.reservacanchas.model.Sede sedeAsociada = new com.reservacanchas.model.Sede();
        sedeAsociada.setId(10L);
        sedeAsociada.setAdministradorSede(existente);

        when(administradorSedeRepository.findById(id)).thenReturn(java.util.Optional.of(existente));
        when(sedeRepository.findByAdministradorSedeId(id)).thenReturn(List.of(sedeAsociada));

        service.eliminarAdministradorSede(id);

        assertNull(sedeAsociada.getAdministradorSede());
        verify(sedeRepository).save(sedeAsociada);
        verify(administradorSedeRepository).delete(existente);
    }
}

