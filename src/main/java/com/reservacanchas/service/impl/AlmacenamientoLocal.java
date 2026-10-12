package com.reservacanchas.service.impl;

import com.reservacanchas.service.IAlmacenamiento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "almacenamiento.tipo", havingValue = "local", matchIfMissing = true)
public class AlmacenamientoLocal implements IAlmacenamiento {

    private final String directorio;

    @Value("${almacenamiento.url.expiracion-segundos:3600}")
    private long expiracionSegundos = 3600;

    @Value("${almacenamiento.url.secreto:reserva-canchas-secreto-firma-clips-2026}")
    private String secretoFirma = "reserva-canchas-secreto-firma-clips-2026";

    public AlmacenamientoLocal(
            @Value("${almacenamiento.local.directorio:target/almacenamiento/clips}") String directorio) {
        this.directorio = directorio;
        try {
            Files.createDirectories(Path.of(directorio));
        } catch (IOException e) {
            // Ignorar o registrar error en creacion de directorio base
        }
    }

    public String getDirectorio() {
        return directorio;
    }

    @Override
    public String subir(File archivo) {
        if (archivo == null || !archivo.exists()) {
            throw new IllegalArgumentException("El archivo a subir no existe");
        }
        try {
            Path dirPath = Path.of(directorio);
            Files.createDirectories(dirPath);
            String extension = "";
            String nombreOriginal = archivo.getName();
            int dotIndex = nombreOriginal.lastIndexOf('.');
            if (dotIndex >= 0) {
                extension = nombreOriginal.substring(dotIndex);
            }
            String nombreDestino = UUID.randomUUID() + extension;
            Path destino = dirPath.resolve(nombreDestino);
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            return nombreDestino;
        } catch (IOException e) {
            throw new RuntimeException("Error al almacenar archivo localmente: " + e.getMessage(), e);
        }
    }

    @Override
    public String generarUrlFirmada(String ruta) {
        return generarUrlFirmada(ruta, expiracionSegundos);
    }

    public String generarUrlFirmada(String ruta, long segundosValidez) {
        if (ruta == null || ruta.isBlank()) {
            return null;
        }
        long expires = Instant.now().getEpochSecond() + segundosValidez;
        String signature = calcularFirma(ruta, expires);
        return "/api/clips/archivos/" + ruta + "?expires=" + expires + "&signature=" + signature;
    }

    @Override
    public boolean validarFirma(String ruta, Long expires, String signature) {
        if (ruta == null || ruta.isBlank() || expires == null || signature == null || signature.isBlank()) {
            return false;
        }
        long ahora = Instant.now().getEpochSecond();
        if (ahora > expires) {
            return false;
        }
        String firmaEsperada = calcularFirma(ruta, expires);
        return MessageDigest.isEqual(
                firmaEsperada.getBytes(StandardCharsets.UTF_8),
                signature.getBytes(StandardCharsets.UTF_8));
    }

    private String calcularFirma(String ruta, long expires) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(
                    secretoFirma.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            String payload = ruta + ":" + expires;
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular firma HMAC: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(String ruta) {
        if (ruta == null || ruta.isBlank()) {
            return;
        }
        try {
            Path archivo = Path.of(directorio).resolve(ruta);
            Files.deleteIfExists(archivo);
        } catch (IOException e) {
            // Ignorar fallo al eliminar
        }
    }
}

