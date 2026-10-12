package com.reservacanchas.model.pago;

import com.reservacanchas.dto.reserva.PagoRequest;
import com.reservacanchas.model.Pago;
import com.reservacanchas.model.PagoBilletera;
import com.reservacanchas.model.PagoEfectivo;
import com.reservacanchas.model.PagoTarjeta;
import com.reservacanchas.model.PagoTransferencia;
import com.reservacanchas.model.Reserva;
import com.reservacanchas.model.enums.EstadoPago;
import com.reservacanchas.model.enums.TipoPago;
import org.springframework.stereotype.Component;

@Component
public class PagoFactory {

    public Pago crear(PagoRequest request, Reserva reserva) {
        Pago pago = switch (request.tipoPago()) {
            case TARJETA -> {
                PagoTarjeta p = new PagoTarjeta();
                p.setUltimosCuatro(request.ultimosCuatro());
                p.setMarca(request.marca());
                yield p;
            }
            case BILLETERA -> {
                PagoBilletera p = new PagoBilletera();
                p.setProveedor(request.proveedor());
                p.setNumeroOperacion(request.numeroOperacion());
                yield p;
            }
            case TRANSFERENCIA -> {
                PagoTransferencia p = new PagoTransferencia();
                p.setBanco(request.banco());
                p.setCodigoOperacion(request.codigoOperacion());
                yield p;
            }
            case EFECTIVO -> {
                PagoEfectivo p = new PagoEfectivo();
                p.setRecibidoPor(request.recibidoPor());
                yield p;
            }
        };

        pago.setMonto(request.monto());
        pago.setEstado(EstadoPago.PENDIENTE);
        pago.setReferencia(request.referencia());
        pago.setReserva(reserva);
        return pago;
    }

    public TipoPago tipoDe(Pago pago) {
        if (pago instanceof PagoTarjeta) {
            return TipoPago.TARJETA;
        }
        if (pago instanceof PagoBilletera) {
            return TipoPago.BILLETERA;
        }
        if (pago instanceof PagoTransferencia) {
            return TipoPago.TRANSFERENCIA;
        }
        if (pago instanceof PagoEfectivo) {
            return TipoPago.EFECTIVO;
        }
        return null;
    }
}
