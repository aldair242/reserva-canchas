package com.reservacanchas.service.impl;

import com.reservacanchas.dto.partido.EventoResponse;
import com.reservacanchas.dto.sensor.EventoSensorRequest;
import com.reservacanchas.dto.sensor.LatidoRequest;
import com.reservacanchas.dto.sensor.LatidoResponse;
import com.reservacanchas.exception.CredencialesInvalidasException;
import com.reservacanchas.exception.OperacionNoPermitidaException;
import com.reservacanchas.mapper.EventoMapper;
import com.reservacanchas.model.CamaraCancha;
import com.reservacanchas.model.Dispositivo;
import com.reservacanchas.model.Gol;
import com.reservacanchas.model.Grabacion;
import com.reservacanchas.model.Partido;
import com.reservacanchas.model.SensorArco;
import com.reservacanchas.model.enums.EstadoEvento;
import com.reservacanchas.model.enums.EstadoGrabacion;
import com.reservacanchas.model.enums.EstadoPartido;
import com.reservacanchas.model.enums.LadoEquipo;
import com.reservacanchas.model.enums.OrigenEvento;
import com.reservacanchas.repository.CamaraCanchaRepository;
import com.reservacanchas.repository.DispositivoRepository;
import com.reservacanchas.repository.EventoPartidoRepository;
import com.reservacanchas.repository.GrabacionRepository;
import com.reservacanchas.repository.PartidoRepository;
import com.reservacanchas.repository.SensorArcoRepository;
import com.reservacanchas.service.ClipService;
import com.reservacanchas.service.SensorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SensorServiceImpl implements SensorService {

    private final SensorArcoRepository sensorArcoRepository;
    private final DispositivoRepository dispositivoRepository;
    private final PartidoRepository partidoRepository;
    private final EventoPartidoRepository eventoRepository;
    private final GrabacionRepository grabacionRepository;
    private final CamaraCanchaRepository camaraCanchaRepository;
    private final ClipService clipService;
    private final EventoMapper eventoMapper;

    @Value("${partidos.atribucion.horas-limite:48}")
    private int horasLimiteAtribucion;

    @Value("${clips.segundos-antes:15}")
    private int segundosAntes;

    @Value("${clips.segundos-despues:8}")
    private int segundosDespues;

    public SensorServiceImpl(
            SensorArcoRepository sensorArcoRepository,
            DispositivoRepository dispositivoRepository,
            PartidoRepository partidoRepository,
            EventoPartidoRepository eventoRepository,
            GrabacionRepository grabacionRepository,
            CamaraCanchaRepository camaraCanchaRepository,
            ClipService clipService,
            EventoMapper eventoMapper) {
        this.sensorArcoRepository = sensorArcoRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.partidoRepository = partidoRepository;
        this.eventoRepository = eventoRepository;
        this.grabacionRepository = grabacionRepository;
        this.camaraCanchaRepository = camaraCanchaRepository;
        this.clipService = clipService;
        this.eventoMapper = eventoMapper;
    }

    private static final String ERROR_CREDENCIALES_INVALIDAS = "Credenciales invalidas del sensor";

    @Override
    @Transactional
    public EventoResponse procesarEvento(EventoSensorRequest request) {
        if (request == null || request.codigoSensor() == null || request.codigoSensor().isBlank()
                || request.claveApi() == null || request.claveApi().isBlank()) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        SensorArco sensor = sensorArcoRepository.findByCodigo(request.codigoSensor())
                .orElseThrow(() -> new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS));

        if (!sensor.validarClave(request.claveApi())) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        if (!sensor.isActivo()) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        sensor.registrarLatido();
        sensorArcoRepository.save(sensor);

        // Localizar el partido EN_JUEGO de esa cancha
        Long canchaId = sensor.getCancha().getId();
        Partido partido = partidoRepository.findByCanchaIdAndEstado(canchaId, EstadoPartido.EN_JUEGO)
                .orElseThrow(() -> new OperacionNoPermitidaException("No hay ningun partido en juego para esta cancha"));

        LocalDateTime instante = request.instante() != null ? request.instante() : LocalDateTime.now();
        LadoEquipo equipo = sensor.deducirEquipoAnotador();

        Gol gol = new Gol();
        gol.setPartido(partido);
        gol.setEquipo(equipo);
        gol.setInstante(instante);
        gol.setMinuto(gol.calcularMinuto(partido.getInicioReal()));
        gol.setOrigen(OrigenEvento.SENSOR);
        gol.setEstado(EstadoEvento.PENDIENTE_ATRIBUCION);
        gol.setFechaLimiteAtribucion(instante.plusHours(horasLimiteAtribucion));

        partido.registrarEvento(gol);
        Gol guardado = eventoRepository.save(gol);

        // Disparar generación asíncrona del clip sin bloquear la respuesta
        Grabacion grabacion = grabacionRepository.findFirstByPartidoIdAndEstado(partido.getId(), EstadoGrabacion.GRABANDO)
                .orElse(null);
        if (grabacion == null) {
            CamaraCancha camara = camaraCanchaRepository.findFirstByCanchaIdAndActivoTrue(canchaId)
                    .orElse(null);
            if (camara != null) {
                grabacion = grabacionRepository.save(camara.iniciarGrabacion(partido));
            }
        }

        if (grabacion != null) {
            clipService.generarClipAsync(grabacion, guardado, instante, segundosAntes, segundosDespues);
        }

        return eventoMapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public LatidoResponse registrarLatido(LatidoRequest request) {
        String codigo = request != null ? request.getCodigoEfectivo() : null;
        String claveApi = request != null ? request.claveApi() : null;

        if (codigo == null || codigo.isBlank() || claveApi == null || claveApi.isBlank()) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        Dispositivo dispositivo = dispositivoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS));

        if (!dispositivo.validarClave(claveApi)) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        if (!dispositivo.isActivo()) {
            throw new CredencialesInvalidasException(ERROR_CREDENCIALES_INVALIDAS);
        }

        dispositivo.registrarLatido();
        Dispositivo guardado = dispositivoRepository.save(dispositivo);

        return new LatidoResponse(guardado.getCodigo(), guardado.getUltimoLatido(), guardado.estaEnLinea());
    }
}

