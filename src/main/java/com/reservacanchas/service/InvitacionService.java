package com.reservacanchas.service;

import com.reservacanchas.dto.partido.InvitacionResponse;
import org.springframework.security.core.Authentication;

public interface InvitacionService {

    InvitacionResponse obtenerResumen(String token);

    void reclamarPerfil(String token, Authentication authentication);
}
