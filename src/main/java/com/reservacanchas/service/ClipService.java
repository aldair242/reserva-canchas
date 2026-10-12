package com.reservacanchas.service;

import com.reservacanchas.dto.clip.ClipDetalleResponse;
import com.reservacanchas.dto.clip.ClipResponse;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.Grabacion;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

public interface ClipService {

    List<ClipResponse> listarClipsPartido(Long partidoId, Authentication authentication);

    ClipDetalleResponse obtenerClip(Long clipId, Authentication authentication);

    ClipResponse descartarClip(Long clipId, Authentication authentication);

    ClipResponse reintentarClip(Long clipId, Authentication authentication);

    void generarClipAsync(Grabacion grabacion, Gol gol, LocalDateTime instante, int antes, int despues);
}

