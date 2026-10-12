package com.reservacanchas.service;

import com.reservacanchas.model.Clip;
import com.reservacanchas.model.Grabacion;

import java.time.LocalDateTime;

public interface IGeneradorClip {

    Clip generar(Grabacion g, LocalDateTime instante, int antes, int despues);
}

