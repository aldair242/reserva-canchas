CREATE TABLE sedes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    distrito VARCHAR(100) NOT NULL,
    latitud DOUBLE PRECISION,
    longitud DOUBLE PRECISION,
    hora_apertura TIME NOT NULL,
    hora_cierre TIME NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    administrador_sede_id BIGINT REFERENCES administradores_sede(id)
);

CREATE INDEX idx_sedes_administrador_sede ON sedes(administrador_sede_id);

CREATE TABLE canchas (
    id BIGSERIAL PRIMARY KEY,
    tipo_cancha VARCHAR(31) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    superficie VARCHAR(30) NOT NULL,
    techada BOOLEAN NOT NULL DEFAULT FALSE,
    iluminacion BOOLEAN NOT NULL DEFAULT FALSE,
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    sede_id BIGINT NOT NULL REFERENCES sedes(id) ON DELETE CASCADE,
    tipo_tarifa VARCHAR(20),
    precio_hora NUMERIC(10, 2),
    precio_normal NUMERIC(10, 2),
    precio_pico NUMERIC(10, 2),
    inicio_pico TIME,
    fin_pico TIME,
    tipo_tarifa_base VARCHAR(20),
    porcentaje_descuento DOUBLE PRECISION DEFAULT 0,
    vigente_desde DATE,
    vigente_hasta DATE
);

CREATE INDEX idx_canchas_sede ON canchas(sede_id);
CREATE INDEX idx_canchas_tipo_cancha ON canchas(tipo_cancha);

CREATE TABLE canchas_futbol (
    id BIGINT PRIMARY KEY REFERENCES canchas(id) ON DELETE CASCADE,
    formato VARCHAR(20) NOT NULL
);

CREATE TABLE canchas_basquet (
    id BIGINT PRIMARY KEY REFERENCES canchas(id) ON DELETE CASCADE,
    medio_campo BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE canchas_voley (
    id BIGINT PRIMARY KEY REFERENCES canchas(id) ON DELETE CASCADE,
    altura_red DOUBLE PRECISION,
    es_de_playa BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE canchas_padel (
    id BIGINT PRIMARY KEY REFERENCES canchas(id) ON DELETE CASCADE,
    paredes_de_vidrio BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE bloqueos_cancha (
    id BIGSERIAL PRIMARY KEY,
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP NOT NULL,
    motivo VARCHAR(255),
    cancha_id BIGINT NOT NULL REFERENCES canchas(id) ON DELETE CASCADE
);

CREATE INDEX idx_bloqueos_cancha ON bloqueos_cancha(cancha_id);
