package com.reservacanchas.service;

import com.reservacanchas.dto.admin.AdministradorSedeResponse;
import com.reservacanchas.dto.admin.CrearAdminSedeRequest;

import java.util.List;

public interface AdminUsuarioService {

    AdministradorSedeResponse crearAdministradorSede(CrearAdminSedeRequest request);

    AdministradorSedeResponse actualizarAdministradorSede(Long id, com.reservacanchas.dto.admin.ActualizarAdminSedeRequest request);

    void eliminarAdministradorSede(Long id);

    List<AdministradorSedeResponse> listarAdministradoresSede();
}

