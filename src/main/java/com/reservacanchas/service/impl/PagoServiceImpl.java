package com.reservacanchas.service.impl;

import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.model.Pago;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.pago.PagoFactory;
import com.reservacanchas.service.PagoService;
import org.springframework.stereotype.Service;

@Service
public class PagoServiceImpl implements PagoService {

    private final PagoFactory pagoFactory;

    public PagoServiceImpl(PagoFactory pagoFactory) {
        this.pagoFactory = pagoFactory;
    }

    @Override
    public Pago procesarPago(Reserva reserva, PagoRequest request) {
        Pago pago = pagoFactory.crear(request, reserva);
        boolean procesado = pago.procesar();
        if (!procesado) {
            throw new IllegalArgumentException("El pago no pudo ser procesado");
        }
        reserva.agregarPago(pago);
        return pago;
    }
}
