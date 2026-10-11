package com.reservacanchas.service.impl;

import com.reservacanchas.dto.cancha.BloqueoCanchaRequest;
import com.reservacanchas.dto.cancha.CanchaRequest;
import com.reservacanchas.dto.cancha.CanchaResponse;
import com.reservacanchas.dto.cancha.DisponibilidadResponse;
import com.reservacanchas.dto.cancha.DisponibilidadResponse.BloqueoInfo;
import com.reservacanchas.dto.cancha.DisponibilidadResponse.TurnoDisponible;
import com.reservacanchas.exception.ResourceNotFoundException;
import com.reservacanchas.mapper.CanchaMapper;
import com.reservacanchas.model.BloqueoCancha;
import com.reservacanchas.model.Cancha;
import com.reservacanchas.model.CanchaBasquet;
import com.reservacanchas.model.CanchaFutbol;
import com.reservacanchas.model.CanchaPadel;
import com.reservacanchas.model.CanchaVoley;
import com.reservacanchas.model.Sede;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.repository.CanchaRepository;
import com.reservacanchas.repository.SedeRepository;
import com.reservacanchas.service.CanchaService;
import com.reservacanchas.service.SecurityService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class CanchaServiceImpl implements CanchaService {

    private final CanchaRepository canchaRepository;
    private final SedeRepository sedeRepository;
    private final CanchaMapper canchaMapper;
    private final SecurityService securityService;

    public CanchaServiceImpl(CanchaRepository canchaRepository,
                             SedeRepository sedeRepository,
                             CanchaMapper canchaMapper,
                             SecurityService securityService) {
        this.canchaRepository = canchaRepository;
        this.sedeRepository = sedeRepository;
        this.canchaMapper = canchaMapper;
        this.securityService = securityService;
    }

    @Override
    @Transactional
    public CanchaResponse crear(CanchaRequest request, Authentication authentication) {
        Sede sede = sedeRepository.findById(request.sedeId())
                .orElseThrow(() -> new ResourceNotFoundException("Sede", request.sedeId()));
        securityService.verificarGestionSede(sede, authentication);

        Cancha cancha = construirCancha(request);
        cancha.setSede(sede);
        Cancha guardada = canchaRepository.save(cancha);
        return canchaMapper.toResponse(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public CanchaResponse obtenerPorId(Long id) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", id));
        return canchaMapper.toResponse(cancha);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CanchaResponse> listarTodas(TipoDeporte deporte, Long sedeId) {
        Class<? extends Cancha> tipo = mapearDeporte(deporte);
        return canchaRepository.buscarConFiltros(sedeId, tipo).stream()
                .map(canchaMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CanchaResponse actualizar(Long id, CanchaRequest request, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", id));
        securityService.verificarGestionSede(cancha.getSede(), authentication);

        if (!request.sedeId().equals(cancha.getSede().getId())) {
            Sede nuevaSede = sedeRepository.findById(request.sedeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sede", request.sedeId()));
            securityService.verificarGestionSede(nuevaSede, authentication);
            cancha.setSede(nuevaSede);
        }

        cancha.setNombre(request.nombre());
        cancha.setSuperficie(request.superficie());
        cancha.setTechada(request.techada());
        cancha.setIluminacion(request.iluminacion());
        cancha.setActiva(request.activa());
        aplicarTarifa(cancha, request);
        aplicarCamposSubtipo(cancha, request);

        Cancha actualizada = canchaRepository.save(cancha);
        return canchaMapper.toResponse(actualizada);
    }

    @Override
    @Transactional
    public void eliminar(Long id, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", id));
        securityService.verificarGestionSede(cancha.getSede(), authentication);
        canchaRepository.delete(cancha);
    }

    @Override
    @Transactional(readOnly = true)
    public DisponibilidadResponse obtenerDisponibilidad(Long canchaId, LocalDate fecha) {
        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", canchaId));

        Sede sede = cancha.getSede();
        LocalTime apertura = sede.getHoraApertura();
        LocalTime cierre = sede.getHoraCierre();

        List<BloqueoInfo> bloqueosInfo = new ArrayList<>();
        List<LocalTime[]> rangosBloqueados = new ArrayList<>();

        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        for (BloqueoCancha b : cancha.getBloqueos()) {
            if (b.getFin().isBefore(inicioDia) || b.getInicio().isAfter(finDia)) {
                continue;
            }
            LocalDateTime bloqueoInicio = b.getInicio().isBefore(inicioDia) ? inicioDia : b.getInicio();
            LocalDateTime bloqueoFin = b.getFin().isAfter(finDia) ? finDia : b.getFin();
            bloqueosInfo.add(new BloqueoInfo(b.getInicio(), b.getFin(), b.getMotivo()));
            rangosBloqueados.add(new LocalTime[]{bloqueoInicio.toLocalTime(), bloqueoFin.toLocalTime()});
        }

        List<TurnoDisponible> turnos = calcularTurnosDisponibles(apertura, cierre, rangosBloqueados);

        return new DisponibilidadResponse(
                canchaId, fecha, apertura, cierre, bloqueosInfo, turnos);
    }

    @Override
    @Transactional
    public CanchaResponse bloquear(Long canchaId, BloqueoCanchaRequest request, Authentication authentication) {
        Cancha cancha = canchaRepository.findById(canchaId)
                .orElseThrow(() -> new ResourceNotFoundException("Cancha", canchaId));
        securityService.verificarGestionSede(cancha.getSede(), authentication);

        BloqueoCancha bloqueo = new BloqueoCancha();
        bloqueo.setInicio(request.inicio());
        bloqueo.setFin(request.fin());
        bloqueo.setMotivo(request.motivo());
        cancha.bloquear(bloqueo);

        Cancha guardada = canchaRepository.save(cancha);
        return canchaMapper.toResponse(guardada);
    }

    private Cancha construirCancha(CanchaRequest request) {
        Cancha cancha = switch (request.tipoDeporte()) {
            case FUTBOL -> {
                CanchaFutbol c = new CanchaFutbol();
                c.setFormato(request.formato());
                yield c;
            }
            case BASQUET -> {
                CanchaBasquet c = new CanchaBasquet();
                c.setMedioCampo(request.medioCampo() != null && request.medioCampo());
                yield c;
            }
            case VOLEY -> {
                CanchaVoley c = new CanchaVoley();
                if (request.alturaRed() != null) {
                    c.setAlturaRed(request.alturaRed());
                }
                c.setEsDePlaya(request.esDePlaya() != null && request.esDePlaya());
                yield c;
            }
            case PADEL -> {
                CanchaPadel c = new CanchaPadel();
                c.setParedesDeVidrio(request.paredesDeVidrio() != null && request.paredesDeVidrio());
                yield c;
            }
        };

        cancha.setNombre(request.nombre());
        cancha.setSuperficie(request.superficie());
        cancha.setTechada(request.techada());
        cancha.setIluminacion(request.iluminacion());
        cancha.setActiva(request.activa());
        aplicarTarifa(cancha, request);
        return cancha;
    }

    private void aplicarTarifa(Cancha cancha, CanchaRequest request) {
        cancha.setTipoTarifa(request.tipoTarifa());
        cancha.setPrecioHora(request.precioHora());
        cancha.setPrecioNormal(request.precioNormal());
        cancha.setPrecioPico(request.precioPico());
        cancha.setInicioPico(request.inicioPico());
        cancha.setFinPico(request.finPico());
        cancha.setTipoTarifaBase(request.tipoTarifaBase());
        cancha.setPorcentajeDescuento(request.porcentajeDescuento());
        cancha.setVigenteDesde(request.vigenteDesde());
        cancha.setVigenteHasta(request.vigenteHasta());
        cancha.setTarifa(null);
    }

    private void aplicarCamposSubtipo(Cancha cancha, CanchaRequest request) {
        if (cancha instanceof CanchaFutbol c) {
            c.setFormato(request.formato());
        } else if (cancha instanceof CanchaBasquet c) {
            c.setMedioCampo(request.medioCampo() != null && request.medioCampo());
        } else if (cancha instanceof CanchaVoley c) {
            if (request.alturaRed() != null) {
                c.setAlturaRed(request.alturaRed());
            }
            c.setEsDePlaya(request.esDePlaya() != null && request.esDePlaya());
        } else if (cancha instanceof CanchaPadel c) {
            c.setParedesDeVidrio(request.paredesDeVidrio() != null && request.paredesDeVidrio());
        }
    }

    private Class<? extends Cancha> mapearDeporte(TipoDeporte deporte) {
        if (deporte == null) {
            return null;
        }
        return switch (deporte) {
            case FUTBOL -> CanchaFutbol.class;
            case BASQUET -> CanchaBasquet.class;
            case VOLEY -> CanchaVoley.class;
            case PADEL -> CanchaPadel.class;
        };
    }

    private List<TurnoDisponible> calcularTurnosDisponibles(
            LocalTime apertura, LocalTime cierre, List<LocalTime[]> bloqueos) {
        List<LocalTime[]> intervalos = new ArrayList<>();
        intervalos.add(new LocalTime[]{apertura, cierre});

        bloqueos.sort(Comparator.comparing(b -> b[0]));

        for (LocalTime[] bloqueo : bloqueos) {
            LocalTime bIni = bloqueo[0];
            LocalTime bFin = bloqueo[1];
            List<LocalTime[]> nuevos = new ArrayList<>();
            for (LocalTime[] intervalo : intervalos) {
                LocalTime iIni = intervalo[0];
                LocalTime iFin = intervalo[1];
                if (!bFin.isAfter(iIni) || !bIni.isBefore(iFin)) {
                    nuevos.add(intervalo);
                    continue;
                }
                if (bIni.isAfter(iIni)) {
                    nuevos.add(new LocalTime[]{iIni, bIni});
                }
                if (bFin.isBefore(iFin)) {
                    nuevos.add(new LocalTime[]{bFin, iFin});
                }
            }
            intervalos = nuevos;
        }

        return intervalos.stream()
                .map(i -> new TurnoDisponible(i[0], i[1]))
                .toList();
    }
}
