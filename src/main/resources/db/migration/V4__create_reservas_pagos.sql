-- Tabla de reservas
-- Se usan columnas generadas (inicio, fin) de tipo TIMESTAMP para poder
-- construir una restriccion EXCLUDE contra reservas solapadas.
CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    inicio TIMESTAMP GENERATED ALWAYS AS (fecha + hora_inicio) STORED,
    fin TIMESTAMP GENERATED ALWAYS AS (fecha + hora_fin) STORED,
    estado VARCHAR(20) NOT NULL,
    precio_total NUMERIC(10, 2) NOT NULL,
    motivo_cancelacion VARCHAR(255),
    version BIGINT NOT NULL DEFAULT 0,
    organizador_id BIGINT NOT NULL REFERENCES jugadores(id),
    cancha_id BIGINT NOT NULL REFERENCES canchas(id) ON DELETE CASCADE,
    CONSTRAINT uk_reservas_codigo UNIQUE (codigo),
    CONSTRAINT reservas_horario_valido CHECK (hora_fin > hora_inicio),
    -- Restriccion de exclusion: no puede haber dos reservas PENDIENTE o CONFIRMADA
    -- de la misma cancha que se solapen en el tiempo.
    CONSTRAINT reservas_sin_solapamiento EXCLUDE USING gist (
        cancha_id WITH =,
        tsrange(inicio, fin) WITH &&
    ) WHERE (estado IN ('PENDIENTE', 'CONFIRMADA'))
);

CREATE INDEX idx_reservas_organizador ON reservas(organizador_id);
CREATE INDEX idx_reservas_cancha ON reservas(cancha_id);
CREATE INDEX idx_reservas_estado ON reservas(estado);

-- Tabla base de pagos (herencia JOINED)
CREATE TABLE pagos (
    id BIGSERIAL PRIMARY KEY,
    tipo_pago VARCHAR(31) NOT NULL,
    monto NUMERIC(10, 2) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_pago TIMESTAMP,
    referencia VARCHAR(100),
    reserva_id BIGINT NOT NULL REFERENCES reservas(id) ON DELETE CASCADE
);

CREATE INDEX idx_pagos_reserva ON pagos(reserva_id);

-- Subtablas de pagos por medio de pago
CREATE TABLE pagos_tarjeta (
    id BIGINT PRIMARY KEY REFERENCES pagos(id) ON DELETE CASCADE,
    ultimos_cuatro VARCHAR(4),
    marca VARCHAR(30)
);

CREATE TABLE pagos_billetera (
    id BIGINT PRIMARY KEY REFERENCES pagos(id) ON DELETE CASCADE,
    proveedor VARCHAR(50),
    numero_operacion VARCHAR(100)
);

CREATE TABLE pagos_transferencia (
    id BIGINT PRIMARY KEY REFERENCES pagos(id) ON DELETE CASCADE,
    banco VARCHAR(50),
    codigo_operacion VARCHAR(100)
);

CREATE TABLE pagos_efectivo (
    id BIGINT PRIMARY KEY REFERENCES pagos(id) ON DELETE CASCADE,
    recibido_por VARCHAR(100)
);
