package com.reservacanchas.service.impl;

import com.reservacanchas.model.Administrador;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.Usuario;
import com.reservacanchas.security.UsuarioDetails;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Override
    public Usuario getUsuarioActual(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioDetails userDetails)) {
            throw new IllegalArgumentException("No hay un usuario autenticado");
        }
        return userDetails.getUsuario();
    }

    @Override
    public boolean esAdministrador(Authentication authentication) {
        return getUsuarioActual(authentication) instanceof Administrador;
    }

    @Override
    public boolean esAdministradorSede(Authentication authentication) {
        return getUsuarioActual(authentication) instanceof AdministradorSede;
    }

    @Override
    public void verificarGestionSede(Sede sede, Authentication authentication) {
        Usuario usuario = getUsuarioActual(authentication);
        if (usuario instanceof Administrador) {
            return;
        }
        if (usuario instanceof AdministradorSede adminSede) {
            if (sede.getAdministradorSede() == null
                    || !adminSede.getId().equals(sede.getAdministradorSede().getId())) {
                throw new IllegalArgumentException(
                        "No tiene permisos para gestionar esta sede");
            }
            return;
        }
        throw new IllegalArgumentException("No tiene permisos para gestionar sedes");
    }
}
