package com.reservacanchas.security;

import com.reservacanchas.model.Administrador;
import com.reservacanchas.model.AdministradorSede;
import com.reservacanchas.model.Jugador;
import com.reservacanchas.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getRol() {
        String role = "JUGADOR";
        if (usuario instanceof Administrador) {
            role = "ADMIN";
        } else if (usuario instanceof AdministradorSede) {
            role = "ADMIN_SEDE";
        }
        return role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = "ROLE_JUGADOR";
        if (usuario instanceof Administrador) {
            role = "ROLE_ADMIN";
        } else if (usuario instanceof AdministradorSede) {
            role = "ROLE_ADMIN_SEDE";
        }
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return usuario.isActivo();
    }
}
