package com.reservacanchas.service.impl;

import com.reservacanchas.model.Gol;
import com.reservacanchas.service.IDetectorGol;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetectorPorSensor implements IDetectorGol {

    @Override
    public List<Gol> detectar() {
        return List.of();
    }
}

