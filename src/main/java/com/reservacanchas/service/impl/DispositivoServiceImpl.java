package com.reservacanchas.service.impl;

import com.reservacanchas.dto.dispositivo.DispositivoRegistroResponse;
import com.reservacanchas.dto.dispositivo.DispositivoResponse;
import com.reservacanchas.dto.dispositivo.RegistroCamaraRequest;
import com.reservacanchas.dto.dispositivo.RegistroSensorRequest;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.DispositivoMapper;
import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.Dispositivo;
import com.reservacanchas.model.SensorArco;
import com.reservacanchas.repository.CanchaRepository;
import com.reservacanchas.repository.DispositivoRepository;
import com.reservacanchas.service.DispositivoService;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DispositivoServiceImpl implements DispositivoService {

    private final DispositivoRepository dispositivoRepository;
    private final CanchaRepository canchaRepository;
    private final DispositivoMapper dispositivoMapper;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;

    public DispositivoServiceImpl(
            DispositivoRepository dispositivoRepository,
            CanchaRepository canchaRepository,
            DispositivoMapper dispositivoMapper,
            PasswordEncoder passwordEncoder,
            SecurityService securityService) {
        this.dispositivoRepository = dispositivoRepository;
        this.canchaRepository = canchaRepository;
        this.dispositivoMapper = dispositivoMapper;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public DispositivoRegistroResponse registrarSensor(Long canchaId, RegistroSensorRequest request, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", canchaId));
        securityService.verificarGestionSede(cancha.getSede(), authentication);

        if (dispositivoRepository.existsByCodigo(request.codigo())) {
            throw new OperacionNoPermitidaException("Ya existe un dispositivo con el codigo: " + request.codigo());
        }

        String claveApi = generarClaveApi();
        SensorArco sensor = new SensorArco();
        sensor.setCodigo(request.codigo());
        sensor.setClaveApiHash(passwordEncoder.encode(claveApi));
        sensor.setActivo(true);
        sensor.setCancha(cancha);
        sensor.setUbicacion(request.ubicacion());
        if (request.sensibilidad() != null) {
            sensor.setSensibilidad(request.sensibilidad());
        }

        SensorArco guardado = dispositivoRepository.save(sensor);
        return dispositivoMapper.toRegistroResponse(guardado, claveApi);
    }

    @Override
    @Transactional
    public DispositivoRegistroResponse registrarCamara(Long canchaId, RegistroCamaraRequest request, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", canchaId));
        securityService.verificarGestionSede(cancha.getSede(), authentication);

        if (dispositivoRepository.existsByCodigo(request.codigo())) {
            throw new OperacionNoPermitidaException("Ya existe un dispositivo con el codigo: " + request.codigo());
        }

        String claveApi = generarClaveApi();
        CamaraCancha camara = new CamaraCancha();
        camara.setCodigo(request.codigo());
        camara.setClaveApiHash(passwordEncoder.encode(claveApi));
        camara.setActivo(true);
        camara.setCancha(cancha);
        camara.setUrlStream(request.urlStream());
        camara.setResolucion(request.resolucion() != null ? request.resolucion() : "1080p");
        if (request.fps() != null) {
            camara.setFps(request.fps());
        }

        CamaraCancha guardada = dispositivoRepository.save(camara);
        return dispositivoMapper.toRegistroResponse(guardada, claveApi);
    }

    @Override
    @Transactional
    public DispositivoResponse desactivar(Long id, Authentication authentication) {
        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));
        securityService.verificarGestionSede(dispositivo.getCancha().getSede(), authentication);

        dispositivo.setActivo(false);
        Dispositivo guardado = dispositivoRepository.save(dispositivo);
        return dispositivoMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DispositivoResponse> listarPorCancha(Long canchaId, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", canchaId));
        securityService.verificarGestionSede(cancha.getSede(), authentication);

        List<Dispositivo> dispositivos = dispositivoRepository.findByCanchaId(canchaId);
        return dispositivos.stream().map(dispositivoMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DispositivoResponse obtenerPorId(Long id, Authentication authentication) {
        Dispositivo dispositivo = dispositivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispositivo", id));
        securityService.verificarGestionSede(dispositivo.getCancha().getSede(), authentication);

        return dispositivoMapper.toResponse(dispositivo);
    }

    private String generarClaveApi() {
        return "DEV-" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}

