package com.reservacanchas.service.impl;

import com.reservacanchas.dto.admin.ActualizarAdminSedeRequest;
import com.reservacanchas.dto.admin.AdministradorSedeResponse;
import com.reservacanchas.dto.admin.CrearAdminSedeRequest;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.Sede;
import com.reservacanchas.repository.AdministradorSedeRepository;
import com.reservacanchas.repository.SedeRepository;
import com.reservacanchas.repository.UsuarioRepository;
import com.reservacanchas.service.AdminUsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminUsuarioServiceImpl implements AdminUsuarioService {

    private final AdministradorSedeRepository administradorSedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final SedeRepository sedeRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUsuarioServiceImpl(AdministradorSedeRepository administradorSedeRepository,
                                  UsuarioRepository usuarioRepository,
                                  SedeRepository sedeRepository,
                                  PasswordEncoder passwordEncoder) {
        this.administradorSedeRepository = administradorSedeRepository;
        this.usuarioRepository = usuarioRepository;
        this.sedeRepository = sedeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public AdministradorSedeResponse crearAdministradorSede(CrearAdminSedeRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el correo: " + request.correo());
        }

        AdministradorSede adminSede = new AdministradorSede();
        adminSede.setNombre(request.nombre());
        adminSede.setApellido(request.apellido());
        adminSede.setCorreo(request.correo());
        adminSede.setTelefono(request.telefono());
        adminSede.setPasswordHash(passwordEncoder.encode(request.password()));
        adminSede.setFechaRegistro(LocalDateTime.now());
        adminSede.setActivo(true);

        AdministradorSede guardado = administradorSedeRepository.save(adminSede);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public AdministradorSedeResponse actualizarAdministradorSede(Long id, ActualizarAdminSedeRequest request) {
        AdministradorSede admin = administradorSedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdministradorSede", id));

        if (!admin.getCorreo().equalsIgnoreCase(request.correo()) && usuarioRepository.existsByCorreo(request.correo())) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con el correo: " + request.correo());
        }

        admin.setNombre(request.nombre());
        admin.setApellido(request.apellido());
        admin.setCorreo(request.correo());
        admin.setTelefono(request.telefono());

        if (request.password() != null && !request.password().trim().isEmpty()) {
            if (request.password().trim().length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            admin.setPasswordHash(passwordEncoder.encode(request.password().trim()));
        }

        if (request.activo() != null) {
            admin.setActivo(request.activo());
        }

        AdministradorSede guardado = administradorSedeRepository.save(admin);
        return toResponse(guardado);
    }

    @Override
    @Transactional
    public void eliminarAdministradorSede(Long id) {
        AdministradorSede admin = administradorSedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdministradorSede", id));

        // Desvincular todas las sedes asociadas para que permanezcan intactas en el sistema
        List<Sede> sedesAsignadas = sedeRepository.findByAdministradorSedeId(id);
        for (Sede s : sedesAsignadas) {
            s.setAdministradorSede(null);
            sedeRepository.save(s);
        }
        admin.getSedes().clear();

        administradorSedeRepository.delete(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdministradorSedeResponse> listarAdministradoresSede() {
        return administradorSedeRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private AdministradorSedeResponse toResponse(AdministradorSede admin) {
        List<Long> sedeIds = admin.getSedes().stream()
                .map(Sede::getId)
                .toList();
        List<String> sedeNombres = admin.getSedes().stream()
                .map(Sede::getNombre)
                .toList();

        return new AdministradorSedeResponse(
                admin.getId(),
                admin.getNombre(),
                admin.getApellido(),
                admin.getCorreo(),
                admin.getTelefono(),
                admin.isActivo(),
                sedeIds,
                sedeNombres
        );
    }
}

