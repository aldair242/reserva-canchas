CREATE TABLE estadisticas_jugador (
    id BIGSERIAL PRIMARY KEY,
    partidos_jugados INTEGER NOT NULL DEFAULT 0,
    goles INTEGER NOT NULL DEFAULT 0,
    asistencias INTEGER NOT NULL DEFAULT 0,
    victorias INTEGER NOT NULL DEFAULT 0,
    empates INTEGER NOT NULL DEFAULT 0,
    derrotas INTEGER NOT NULL DEFAULT 0
);

CREATE TABLE personas (
    id BIGSERIAL PRIMARY KEY,
    tipo_persona VARCHAR(31) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20)
);

CREATE TABLE usuarios (
    id BIGINT PRIMARY KEY REFERENCES personas(id) ON DELETE CASCADE,
    correo VARCHAR(150) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    fecha_registro TIMESTAMP NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_usuarios_correo UNIQUE (correo)
);

CREATE TABLE jugadores (
    id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE,
    apodo VARCHAR(50),
    foto_url VARCHAR(500),
    posicion_preferida VARCHAR(50),
    estadistica_id BIGINT REFERENCES estadisticas_jugador(id)
);

CREATE TABLE administradores_sede (
    id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE administradores (
    id BIGINT PRIMARY KEY REFERENCES usuarios(id) ON DELETE CASCADE
);

CREATE TABLE invitados (
    id BIGINT PRIMARY KEY REFERENCES personas(id) ON DELETE CASCADE,
    token_reclamo VARCHAR(100),
    reclamado BOOLEAN NOT NULL DEFAULT FALSE
);
