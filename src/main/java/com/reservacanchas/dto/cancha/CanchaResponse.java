package com.reservacanchas.dto.cancha;

import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Detalle completo de una cancha deportiva y sus caracteristicas polimorficas")
public record CanchaResponse(
        @Schema(description = "Identificador unico de la cancha", example = "1")
        Long id,

        @Schema(description = "Nombre de la cancha", example = "Cancha Principal Futbol 7")
        String nombre,

        @Schema(description = "Superficie de la cancha", example = "CESPED_SINTETICO")
        TipoSuperficie superficie,

        @Schema(description = "Indica si es techada", example = "false")
        boolean techada,

        @Schema(description = "Indica si cuenta con iluminacion", example = "true")
        boolean iluminacion,

        @Schema(description = "Indica si la cancha esta activa", example = "true")
        boolean activa,

        @Schema(description = "Id de la sede a la que pertenece", example = "1")
        Long sedeId,

        @Schema(description = "Nombre de la sede", example = "Sede Miraflores")
        String sedeNombre,

        @Schema(description = "Deporte praticado", example = "FUTBOL")
        TipoDeporte deporte,

        @Schema(description = "Capacidad maxima reglamentaria de jugadores", example = "14")
        int capacidadJugadores,

        @Schema(description = "Formato de juego (solo futbol)", example = "FUTBOL_7")
        FormatoFutbol formato,

        @Schema(description = "Medio campo (solo basquet)", example = "false")
        Boolean medioCampo,

        @Schema(description = "Altura de red en metros (solo voley)", example = "2.43")
        Double alturaRed,

        @Schema(description = "Es de playa (solo voley)", example = "false")
        Boolean esDePlaya,

        @Schema(description = "Paredes de vidrio (solo padel)", example = "true")
        Boolean paredesDeVidrio,

        @Schema(description = "Tipo de estrategia tarifaria activa", example = "ESTANDAR")
        TipoTarifa tipoTarifa,

        @Schema(description = "Precio por hora estandar", example = "80.00")
        BigDecimal precioHora,

        @Schema(description = "Precio por hora en horario normal", example = "70.00")
        BigDecimal precioNormal,

        @Schema(description = "Precio por hora en horario pico", example = "100.00")
        BigDecimal precioPico,

        @Schema(description = "Hora inicio del bloque pico", example = "18:00:00")
        LocalTime inicioPico,

        @Schema(description = "Hora fin del bloque pico", example = "23:00:00")
        LocalTime finPico,

        @Schema(description = "Tipo de tarifa base sobre la que aplica promocion", example = "ESTANDAR")
        TipoTarifa tipoTarifaBase,

        @Schema(description = "Porcentaje de descuento", example = "15.0")
        double porcentajeDescuento,

        @Schema(description = "Fecha desde la que rige la promocion", example = "2026-10-01")
        LocalDate vigenteDesde,

        @Schema(description = "Fecha hasta la que rige la promocion", example = "2026-10-31")
        LocalDate vigenteHasta
) {
}
