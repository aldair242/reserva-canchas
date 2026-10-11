package com.reservacanchas.service;

import com.reservacanchas.model.Sede;
import com.reservacanchas.model.Usuario;
import org.springframework.security.core.Authentication;

public interface SecurityService {

    Usuario getUsuarioActual(Authentication authentication);

    boolean esAdministrador(Authentication authentication);

    boolean esAdministradorSede(Authentication authentication);

    void verificarGestionSede(Sede sede, Authentication authentication);
}
