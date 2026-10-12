-- Tabla base de dispositivos (herencia JOINED)
CREATE TABLE dispositivos (
    id BIGSERIAL PRIMARY KEY,
    tipo_dispositivo VARCHAR(31) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    clave_api_hash VARCHAR(255) NOT NULL,
    ultimo_latido TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    cancha_id BIGINT NOT NULL REFERENCES canchas(id) ON DELETE CASCADE
);

CREATE INDEX idx_dispositivos_cancha ON dispositivos(cancha_id);
CREATE INDEX idx_dispositivos_codigo ON dispositivos(codigo);

-- Subtabla para sensores de arco
CREATE TABLE sensores_arco (
    id BIGINT PRIMARY KEY REFERENCES dispositivos(id) ON DELETE CASCADE,
    ubicacion VARCHAR(20) NOT NULL,
    sensibilidad INTEGER NOT NULL DEFAULT 50
);

-- Subtabla para cámaras de cancha
CREATE TABLE camaras_cancha (
    id BIGINT PRIMARY KEY REFERENCES dispositivos(id) ON DELETE CASCADE,
    url_stream VARCHAR(255),
    resolucion VARCHAR(20),
    fps INTEGER
);

-- Tabla de grabaciones
CREATE TABLE grabaciones (
    id BIGSERIAL PRIMARY KEY,
    url_base VARCHAR(255),
    inicio TIMESTAMP NOT NULL,
    fin TIMESTAMP,
    estado VARCHAR(20) NOT NULL,
    camara_id BIGINT NOT NULL REFERENCES camaras_cancha(id),
    partido_id BIGINT NOT NULL REFERENCES partidos(id) ON DELETE CASCADE
);

CREATE INDEX idx_grabaciones_camara ON grabaciones(camara_id);
CREATE INDEX idx_grabaciones_partido ON grabaciones(partido_id);

-- Tabla de clips
CREATE TABLE clips (
    id BIGSERIAL PRIMARY KEY,
    url_video VARCHAR(255),
    url_miniatura VARCHAR(255),
    segundos_antes INTEGER NOT NULL DEFAULT 15,
    segundos_despues INTEGER NOT NULL DEFAULT 8,
    duracion_segundos INTEGER,
    instante TIMESTAMP,
    estado VARCHAR(20) NOT NULL,
    visible BOOLEAN NOT NULL DEFAULT TRUE,
    vistas INTEGER NOT NULL DEFAULT 0,
    grabacion_id BIGINT NOT NULL REFERENCES grabaciones(id) ON DELETE CASCADE,
    partido_id BIGINT NOT NULL REFERENCES partidos(id) ON DELETE CASCADE
);

CREATE INDEX idx_clips_grabacion ON clips(grabacion_id);
CREATE INDEX idx_clips_partido ON clips(partido_id);

-- Relación entre Gol y Clip
ALTER TABLE eventos_gol ADD COLUMN clip_id BIGINT REFERENCES clips(id);
CREATE INDEX idx_eventos_gol_clip ON eventos_gol(clip_id);
