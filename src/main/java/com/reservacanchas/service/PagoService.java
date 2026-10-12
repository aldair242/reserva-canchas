package com.reservacanchas.service;

import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.model.Pago;
import com.reservacanchas.model.Reserva;

public interface PagoService {

    Pago procesarPago(Reserva reserva, PagoRequest request);
}
