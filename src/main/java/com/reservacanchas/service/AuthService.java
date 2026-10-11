package com.reservacanchas.service;

import com.reservacanchas.dto.auth.AuthResponse;
import com.reservacanchas.dto.auth.LoginRequest;
import com.reservacanchas.dto.auth.RegistroRequest;

public interface AuthService {

    AuthResponse registrar(RegistroRequest request);

    AuthResponse login(LoginRequest request);
}
