package com.reservacanchas.service;

import com.reservacanchas.dto.admin.AdministradorSedeResponse;
import com.reservacanchas.dto.admin.CrearAdminSedeRequest;

import java.util.List;

public interface AdminUsuarioService {

    AdministradorSedeResponse crearAdministradorSede(CrearAdminSedeRequest request);

    List<AdministradorSedeResponse> listarAdministradoresSede();
}

