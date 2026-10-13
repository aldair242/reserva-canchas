package com.reservacanchas.config;

import com.reservacanchas.model.Administrador;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.repository.AdministradorRepository;
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

    private static final String CORREO_ADMIN_SEDE = "admin@prueba.com";
    private static final String PASSWORD_ADMIN_SEDE = "admin123";

    private static final String CORREO_ADMIN_GENERAL = "admingeneral@prueba.com";
    private static final String PASSWORD_ADMIN_GENERAL = "admin123";

    private final UsuarioRepository usuarioRepository;
    private final AdministradorSedeRepository administradorSedeRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public DevDataInitializer(UsuarioRepository usuarioRepository,
                              AdministradorSedeRepository administradorSedeRepository,
                              AdministradorRepository administradorRepository,
                              PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.administradorSedeRepository = administradorSedeRepository;
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        crearAdminSedeSiNoExiste();
        crearAdminGeneralSiNoExiste(CORREO_ADMIN_GENERAL, PASSWORD_ADMIN_GENERAL);
        crearAdminGeneralSiNoExiste("admin.general@prueba.com", "admin123");
    }

    private void crearAdminSedeSiNoExiste() {
        if (!usuarioRepository.existsByCorreo(CORREO_ADMIN_SEDE)) {
            AdministradorSede admin = new AdministradorSede();
            admin.setNombre("Admin");
            admin.setApellido("Sede");
            admin.setTelefono("999999999");
            admin.setCorreo(CORREO_ADMIN_SEDE);
            admin.setPasswordHash(passwordEncoder.encode(PASSWORD_ADMIN_SEDE));
            admin.setFechaRegistro(LocalDateTime.now());
            admin.setActivo(true);

            administradorSedeRepository.save(admin);
            log.info("Usuario AdministradorSede de prueba creado: {} (password: {})", CORREO_ADMIN_SEDE, PASSWORD_ADMIN_SEDE);
        }
    }

    private void crearAdminGeneralSiNoExiste(String correo, String password) {
        if (!usuarioRepository.existsByCorreo(correo)) {
            Administrador admin = new Administrador();
            admin.setNombre("Admin");
            admin.setApellido("General");
            admin.setTelefono("999888777");
            admin.setCorreo(correo);
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setFechaRegistro(LocalDateTime.now());
            admin.setActivo(true);

            administradorRepository.save(admin);
            log.info("Usuario Administrador (Admin General) de prueba creado: {} (password: {})", correo, password);
        }
    }
}
