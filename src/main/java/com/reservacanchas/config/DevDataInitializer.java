package com.reservacanchas.config;

import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.repository.AdministradorSedeRepository;
import com.reservacanchas.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("dev")
public class DevDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    private static final String CORREO_ADMIN = "admin@prueba.com";
    private static final String PASSWORD_ADMIN = "admin123";

    private final UsuarioRepository usuarioRepository;
    private final AdministradorSedeRepository administradorSedeRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UsuarioRepository usuarioRepository,
                              AdministradorSedeRepository administradorSedeRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.administradorSedeRepository = administradorSedeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.existsByCorreo(CORREO_ADMIN)) {
            return;
        }

        AdministradorSede admin = new AdministradorSede();
        admin.setNombre("Admin");
        admin.setApellido("Sede");
        admin.setTelefono("999999999");
        admin.setCorreo(CORREO_ADMIN);
        admin.setPasswordHash(passwordEncoder.encode(PASSWORD_ADMIN));
        admin.setFechaRegistro(LocalDateTime.now());
        admin.setActivo(true);

        administradorSedeRepository.save(admin);
        log.info("Usuario AdministradorSede de prueba creado: {} (password: {})", CORREO_ADMIN, PASSWORD_ADMIN);
    }
}
