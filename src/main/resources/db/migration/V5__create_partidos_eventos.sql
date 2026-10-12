-- Tabla de partidos (uno por reserva confirmada y pagada)
CREATE TABLE partidos (
    id BIGSERIAL PRIMARY KEY,
    estado VARCHAR(20) NOT NULL,
    nombre_equipo_a VARCHAR(100),
    nombre_equipo_b VARCHAR(100),
    goles_equipo_a INTEGER NOT NULL DEFAULT 0,
    goles_equipo_b INTEGER NOT NULL DEFAULT 0,
    inicio_real TIMESTAMP,
    fin_real TIMESTAMP,
    token_invitacion VARCHAR(100) NOT NULL,
    reserva_id BIGINT NOT NULL UNIQUE REFERENCES reservas(id),
    CONSTRAINT uk_partidos_token_invitacion UNIQUE (token_invitacion)
);

CREATE INDEX idx_partidos_reserva ON partidos(reserva_id);

-- Tabla de participantes de un partido (referencia a Persona: Jugador o Invitado)
CREATE TABLE participantes_partido (
    id BIGSERIAL PRIMARY KEY,
    equipo VARCHAR(10) NOT NULL,
    asistio BOOLEAN NOT NULL DEFAULT FALSE,
    partido_id BIGINT NOT NULL REFERENCES partidos(id) ON DELETE CASCADE,
    persona_id BIGINT NOT NULL REFERENCES personas(id)
);

CREATE INDEX idx_participantes_partido ON participantes_partido(partido_id);
CREATE INDEX idx_participantes_persona ON participantes_partido(persona_id);

-- Tabla base de eventos del partido (herencia JOINED)
CREATE TABLE eventos_partido (
    id BIGSERIAL PRIMARY KEY,
    tipo_evento VARCHAR(31) NOT NULL,
    equipo VARCHAR(10) NOT NULL,
    instante TIMESTAMP NOT NULL,
    minuto INTEGER NOT NULL DEFAULT 0,
    origen VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    partido_id BIGINT NOT NULL REFERENCES partidos(id) ON DELETE CASCADE,
    atribuidor_id BIGINT REFERENCES usuarios(id),
    confirmador_id BIGINT REFERENCES usuarios(id)
);

CREATE INDEX idx_eventos_partido ON eventos_partido(partido_id);
CREATE INDEX idx_eventos_estado ON eventos_partido(estado);

-- Subtablas de eventos
CREATE TABLE eventos_gol (
    id BIGINT PRIMARY KEY REFERENCES eventos_partido(id) ON DELETE CASCADE,
    fecha_limite_atribucion TIMESTAMP,
    anotador_id BIGINT REFERENCES participantes_partido(id),
    asistente_id BIGINT REFERENCES participantes_partido(id)
);

CREATE TABLE eventos_gol_en_contra (
    id BIGINT PRIMARY KEY REFERENCES eventos_partido(id) ON DELETE CASCADE,
    autor_id BIGINT NOT NULL REFERENCES participantes_partido(id)
);

CREATE TABLE eventos_tarjeta (
    id BIGINT PRIMARY KEY REFERENCES eventos_partido(id) ON DELETE CASCADE,
    motivo VARCHAR(255),
    jugador_id BIGINT NOT NULL REFERENCES participantes_partido(id)
);

CREATE TABLE eventos_tarjeta_amarilla (
    id BIGINT PRIMARY KEY REFERENCES eventos_tarjeta(id) ON DELETE CASCADE
);

CREATE TABLE eventos_tarjeta_roja (
    id BIGINT PRIMARY KEY REFERENCES eventos_tarjeta(id) ON DELETE CASCADE
);

-- Columna para registrar el Jugador que reclamó el perfil de un Invitado
ALTER TABLE invitados ADD COLUMN reclamado_por_id BIGINT REFERENCES usuarios(id);
CREATE INDEX idx_invitados_reclamado_por ON invitados(reclamado_por_id);
