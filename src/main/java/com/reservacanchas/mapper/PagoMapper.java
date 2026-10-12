package com.reservacanchas.mapper;

import com.reservacanchas.dto.reserva.PagoResponse;
import com.reservacanchas.model.Pago;
import com.reservacanchas.model.PagoBilletera;
import com.reservacanchas.model.PagoEfectivo;
import com.reservacanchas.model.PagoTarjeta;
import com.reservacanchas.model.PagoTransferencia;
import com.reservacanchas.model.enums.TipoPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface PagoMapper {

    @Mapping(target = "tipoPago", expression = "java(tipoPago(pago))")
    PagoResponse toResponse(Pago pago);

    default TipoPago tipoPago(Pago pago) {
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
