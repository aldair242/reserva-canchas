package com.reservacanchas.service.impl;

import com.reservacanchas.model.Gol;
import com.reservacanchas.service.IDetectorGol;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DetectorPorYolo implements IDetectorGol {

    private String modelo = "yolov8-sports.pt";
    private double umbralConfianza = 0.85;

    public DetectorPorYolo() {
    }

    public DetectorPorYolo(String modelo, double umbralConfianza) {
        this.modelo = modelo;
        this.umbralConfianza = umbralConfianza;
    }

    @Override
    public List<Gol> detectar() {
        // PENDIENTE: Integracion futura con modelo YOLO para deteccion de goles mediante vision artificial
        throw new UnsupportedOperationException("Deteccion por YOLO pendiente de implementacion");
    }
}

