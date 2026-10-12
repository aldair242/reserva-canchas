package com.reservacanchas.service.impl;

import com.reservacanchas.service.IAlmacenamiento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@ConditionalOnProperty(name = "almacenamiento.tipo", havingValue = "s3")
public class AlmacenamientoS3 implements IAlmacenamiento {

    private final String bucket;

    public AlmacenamientoS3(
            @Value("${almacenamiento.s3.bucket:reservacanchas-clips}") String bucket) {
        this.bucket = bucket;
    }

    public String getBucket() {
        return bucket;
    }

    @Override
    public String subir(File archivo) {
        // PENDIENTE: Integracion con AWS S3 SDK para subida de objetos al bucket
        throw new UnsupportedOperationException("Almacenamiento S3 pendiente de implementacion");
    }

    @Override
    public String generarUrlFirmada(String ruta) {
        // PENDIENTE: Generacion de presigned URL con AWS S3 SDK
        throw new UnsupportedOperationException("Almacenamiento S3 pendiente de implementacion");
    }

    @Override
    public void eliminar(String ruta) {
        // PENDIENTE: Eliminacion de objeto en AWS S3
        throw new UnsupportedOperationException("Almacenamiento S3 pendiente de implementacion");
    }
}

