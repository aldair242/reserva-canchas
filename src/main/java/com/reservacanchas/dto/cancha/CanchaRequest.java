package com.reservacanchas.dto.cancha;

import com.reservacanchas.model.enums.FormatoFutbol;
import com.reservacanchas.model.enums.TipoDeporte;
import com.reservacanchas.model.enums.TipoSuperficie;
import com.reservacanchas.model.enums.TipoTarifa;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Solicitud de creacion o actualizacion de cancha")
public record CanchaRequest(
        @NotBlank
        @Schema(description = "Nombre identificador de la cancha", example = "Cancha Sintetica 1", requiredMode = Schema.RequiredMode.REQUIRED)
        String nombre,

        @NotNull
        @Schema(description = "Tipo de superficie", example = "CESPED_SINTETICO", requiredMode = Schema.RequiredMode.REQUIRED)
        TipoSuperficie superficie,

        @Schema(description = "Indica si la cancha es techada", example = "false")
        boolean techada,

        @Schema(description = "Indica si cuenta con iluminacion artificial", example = "true")
        boolean iluminacion,

        @Schema(description = "Indica si la cancha esta activa y disponible", example = "true")
        boolean activa,

        @NotNull
        @Schema(description = "Identificador de la sede donde se ubica", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        Long sedeId,

        @NotNull
        @Schema(description = "Disciplina deportiva (FUTBOL, BASQUET, VOLEY, PADEL)", example = "FUTBOL", requiredMode = Schema.RequiredMode.REQUIRED)
        TipoDeporte tipoDeporte,

        @Schema(description = "Formato de juego si es futbol (FUTBOL_5, FUTBOL_7, FUTBOL_11)", example = "FUTBOL_7")
        FormatoFutbol formato,

        @Schema(description = "Indica si es medio campo (para basquet)", example = "false")
        Boolean medioCampo,

        @Schema(description = "Altura de la red en metros (para voley)", example = "2.43")
        Double alturaRed,

        @Schema(description = "Indica si es cancha de voley playa", example = "false")
        Boolean esDePlaya,

        @Schema(description = "Indica si tiene paredes de vidrio (para padel)", example = "true")
        Boolean paredesDeVidrio,

        @NotNull
        @Schema(description = "Estrategia tarifaria (ESTANDAR, HORA_PICO, PROMOCIONAL)", example = "ESTANDAR", requiredMode = Schema.RequiredMode.REQUIRED)
        TipoTarifa tipoTarifa,

        @Schema(description = "Precio por hora (tarifa estandar)", example = "80.00")
        BigDecimal precioHora,

        @Schema(description = "Precio por hora en horario normal (tarifa hora pico)", example = "70.00")
        BigDecimal precioNormal,

        @Schema(description = "Precio por hora en horario pico (tarifa hora pico)", example = "100.00")
        BigDecimal precioPico,

        @Schema(description = "Hora de inicio del horario pico (HH:mm:ss)", example = "18:00:00")
        LocalTime inicioPico,

        @Schema(description = "Hora de fin del horario pico (HH:mm:ss)", example = "23:00:00")
        LocalTime finPico,

        @Schema(description = "Tipo de tarifa base sobre la que aplica descuento promocional", example = "ESTANDAR")
        TipoTarifa tipoTarifaBase,

        @Schema(description = "Porcentaje de descuento (0 a 100)", example = "15.0")
        double porcentajeDescuento,

        @Schema(description = "Fecha inicial de vigencia de la promocion", example = "2026-10-01")
        LocalDate vigenteDesde,

        @Schema(description = "Fecha final de vigencia de la promocion", example = "2026-10-31")
        LocalDate vigenteHasta
) {
}
