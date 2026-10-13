# Reserva de Canchas Deportivas

Sistema integral para la reserva de canchas deportivas, gestión de partidos, estadísticas individuales de jugadores, integración con sensores de arco IoT y generación automatizada de clips de jugadas destacadas (highlights).

---

## 🛠 Requisitos Previos

- **Java 21 (JDK 21)**
- **Docker Desktop** (en ejecución, necesario para PostgreSQL y la suite de pruebas con Testcontainers)
- **Maven** (o el wrapper `mvnw` / `mvnw.cmd` incluido)
- *(Opcional)* **FFmpeg** instalado en el sistema (solo si se desactiva el modo simulado de generación de clips)

---

## 🚀 Puesta en Marcha

### 1. Iniciar la Base de Datos con Docker Compose

Levanta PostgreSQL 16 con la extensión `btree_gist` habilitada:

```bash
docker compose up -d
```

Configuración por defecto del contenedor:
- **Host**: `localhost:5432`
- **Base de datos**: `reservacanchas`
- **Usuario**: `reservacanchas`
- **Contraseña**: `reservacanchas`

### 2. Ejecutar la Aplicación

Con el perfil `dev` activo por defecto:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

O empaquetando el ejecutable JAR:

```bash
.\mvnw.cmd package -DskipTests
java -jar target/reserva-canchas-0.0.1-SNAPSHOT.jar
```

La aplicación se iniciará en `http://localhost:8080`.

---

## ⚙️ Variables de Entorno y Configuración

| Variable de Entorno | Propiedad Spring | Valor por Defecto | Descripción |
|---|---|---|---|
| `JWT_SECRET` | `jwt.secret` | Clave predefinida en dev | Secreto HMAC-SHA256 (mínimo 256 bits). Obligatorio en producción. |
| `JWT_EXPIRATION` | `jwt.expiration` | `86400000` (24 horas) | Tiempo de expiración del token JWT en milisegundos. |
| `CORS_ALLOWED_ORIGINS` | `cors.allowed-origins` | `http://localhost:3000,http://localhost:4200` en dev, vacío en prod | Orígenes permitidos para CORS (separados por coma). |
| `CLIPS_FFMPEG_SIMULADO` | `clips.ffmpeg.simulado` | `true` | `true` para generar archivos de marcador simulados sin binario FFmpeg; `false` para ejecutar FFmpeg real. |
| `CLIPS_FFMPEG_RUTA` | `clips.ffmpeg.ruta` | `ffmpeg` | Ruta al ejecutable de FFmpeg en el sistema operativo. |
| `CLIPS_SEGUNDOS_ANTES` | `clips.segundos-antes` | `15` | Segundos de video antes del evento a incluir en el clip. |
| `CLIPS_SEGUNDOS_DESPUES` | `clips.segundos-despues` | `8` | Segundos de video posteriores al evento a incluir en el clip. |
| `ALMACENAMIENTO_TIPO` | `almacenamiento.tipo` | `local` | Proveedor activo de almacenamiento de clips (`local` o `s3`). |
| `ALMACENAMIENTO_LOCAL_DIR` | `almacenamiento.local.directorio` | `target/almacenamiento/clips` | Directorio local donde se guardan los clips y miniaturas. |
| `ALMACENAMIENTO_S3_BUCKET` | `almacenamiento.s3.bucket` | `reservacanchas-clips` | Nombre del bucket AWS S3 (para implementación S3). |

---

## 📖 Documentación Swagger / OpenAPI

La API cuenta con documentación interactiva OpenAPI 3 / Swagger:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **Especificación OpenAPI (JSON)**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧪 Ejecución de Pruebas

La suite de pruebas ejecuta tests unitarios y de integración con base de datos real gestionada vía **Testcontainers**:

> **Nota**: Asegúrate de tener Docker Desktop iniciado antes de ejecutar los tests.

```bash
# Windows
.\mvnw.cmd clean verify

# Linux / macOS
./mvnw clean verify
```

Para ejecutar una prueba específica:

```bash
.\mvnw.cmd test -Dtest=ReservaSolapamientoIntegrationTest
```

---

## 🔄 Flujo Completo de la API (Ejemplos cURL)

> **Usuarios iniciales de prueba (`dev` profile)**:
> - **Administrador Sede**: `admin@prueba.com` / `admin123` (Rol: `ADMIN_SEDE`)
> - **Administrador General**: `admingeneral@prueba.com` o `admin.general@prueba.com` / `admin123` (Rol: `ADMIN`)

### 1. Registro de Jugador

```bash
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Lionel",
    "apellido": "Messi",
    "telefono": "987654321",
    "correo": "jugador@prueba.com",
    "password": "password123",
    "posicionHabitual": "DELANTERO",
    "pieHabil": "ZURDO"
  }'
```

### 2. Login de Jugador y Administrador

**Login Jugador:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "jugador@prueba.com",
    "password": "password123"
  }'
```
*(Guarda el `token` retornado como `TOKEN_JUGADOR`)*

**Login Administrador:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@prueba.com",
    "password": "admin123"
  }'
```
*(Guarda el `token` retornado como `TOKEN_ADMIN`)*

### 3. Crear Sede (Administrador)

```bash
curl -X POST http://localhost:8080/api/sedes \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Sede Central Palermo",
    "direccion": "Av. Libertador 1234",
    "ciudad": "Buenos Aires",
    "telefono": "1145678901",
    "horaApertura": "08:00:00",
    "horaCierre": "23:00:00"
  }'
```
*(Supongamos que devuelve la sede con `id = 1`)*

### 4. Crear Cancha (Administrador)

```bash
curl -X POST http://localhost:8080/api/canchas \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "sedeId": 1,
    "nombre": "Cancha 1 - Fútbol 7",
    "deporte": "FUTBOL",
    "tipoSuperficie": "SINTETICO",
    "techada": false,
    "activa": true,
    "formato": "FUTBOL_7",
    "precioBase": 100.0,
    "tipoTarifa": "PRECIO_FIJO"
  }'
```
*(Supongamos que devuelve la cancha con `id = 1`)*

### 5. Registrar Sensor y Cámara en la Cancha (Administrador)

**Registrar Sensor de Arco:**
```bash
curl -X POST http://localhost:8080/api/dispositivos/sensores?canchaId=1 \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "SN-ARCO-NORTE-1",
    "ubicacion": "ARCO_NORTE",
    "sensibilidad": 80
  }'
```
> **Nota**: La respuesta contiene `claveApi` en texto claro por única vez. Guarda `codigo` y `claveApi`.

**Registrar Cámara:**
```bash
curl -X POST http://localhost:8080/api/dispositivos/camaras?canchaId=1 \
  -H "Authorization: Bearer $TOKEN_ADMIN" \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "CAM-CANCHA-1",
    "urlStream": "rtsp://camera1.local/live",
    "resolucion": "1080p",
    "fps": 30
  }'
```

### 6. Consultar Disponibilidad de la Cancha (Público)

```bash
curl -X GET "http://localhost:8080/api/canchas/1/disponibilidad?fecha=2026-10-20"
```

### 7. Crear Reserva (Jugador)

```bash
curl -X POST http://localhost:8080/api/reservas \
  -H "Authorization: Bearer $TOKEN_JUGADOR" \
  -H "Content-Type: application/json" \
  -d '{
    "canchaId": 1,
    "fecha": "2026-10-20",
    "horaInicio": "18:00:00",
    "horaFin": "19:00:00"
  }'
```
*(Supongamos que devuelve la reserva con `id = 1` en estado `PENDIENTE_PAGO`)*

### 8. Pagar la Reserva (Jugador)

```bash
curl -X POST http://localhost:8080/api/reservas/1/pagar \
  -H "Authorization: Bearer $TOKEN_JUGADOR" \
  -H "Content-Type: application/json" \
  -d '{
    "metodoPago": "TARJETA_CREDITO",
    "codigoComprobante": "PAY-12345678"
  }'
```
*(La reserva pasa a estado `CONFIRMADA`)*

### 9. Crear e Iniciar Partido (Jugador / Organizador)

**Crear Partido asociado a la Reserva:**
```bash
curl -X POST http://localhost:8080/api/partidos \
  -H "Authorization: Bearer $TOKEN_JUGADOR" \
  -H "Content-Type: application/json" \
  -d '{
    "reservaId": 1,
    "nombreEquipoA": "Los Galácticos",
    "nombreEquipoB": "Furia FC"
  }'
```
*(Devuelve el partido con `id = 1`)*

**Iniciar Partido:**
```bash
curl -X POST http://localhost:8080/api/partidos/1/iniciar \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```
*(El partido pasa a `EN_JUEGO` y se inicia la grabación del partido)*

### 10. Evento de Sensor de Gol (IoT / Dispositivo)

Autenticación mediante `codigoSensor` y `claveApi` en el cuerpo:

```bash
curl -X POST http://localhost:8080/api/sensores/eventos \
  -H "Content-Type: application/json" \
  -d '{
    "codigoSensor": "SN-ARCO-NORTE-1",
    "claveApi": "<TU_CLAVE_API_DEL_PASO_5>",
    "instante": "2026-10-20T18:25:00"
  }'
```
*(Crea un `Gol` en estado `PENDIENTE_ATRIBUCION` con origen `SENSOR`, deduce el equipo según el arco, y dispara asíncronamente el procesamiento del clip de video)*

### 11. Atribuir Gol al Jugador y Confirmar (Jugador / Organizador)

**Atribuir Gol:**
```bash
curl -X POST http://localhost:8080/api/partidos/1/eventos/1/atribuir \
  -H "Authorization: Bearer $TOKEN_JUGADOR" \
  -H "Content-Type: application/json" \
  -d '{
    "jugadorId": 2,
    "asistenteId": null
  }'
```

**Confirmar Gol:**
```bash
curl -X PUT http://localhost:8080/api/partidos/1/eventos/1/confirmar \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```
*(El gol pasa a `CONFIRMADO` y actualiza el marcador del partido)*

### 12. Finalizar Partido (Jugador / Organizador)

```bash
curl -X POST http://localhost:8080/api/partidos/1/finalizar \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```
*(El partido pasa a `FINALIZADO` y se computan las estadísticas de los participantes)*

### 13. Consultar Clips y URL Firmada (Participante / Administrador)

**Listar clips del partido:**
```bash
curl -X GET http://localhost:8080/api/partidos/1/clips \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```

**Obtener detalle de clip con URL firmada:**
```bash
curl -X GET http://localhost:8080/api/clips/1 \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```
*(Devuelve `urlVideoFirmada` con parámetros `exp` y `sig` con expiración de 1 hora)*

**Reproducir o descargar video:**
```bash
curl -X GET "http://localhost:8080/api/clips/archivos/<UUID>.mp4?exp=<timestamp>&sig=<hash>"
```

### 14. Consultar Estadísticas del Jugador y Rankings (Público / Autenticado)

**Estadísticas individuales:**
```bash
curl -X GET http://localhost:8080/api/jugadores/2/estadisticas \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```

**Ranking de goleadores:**
```bash
curl -X GET http://localhost:8080/api/rankings/goleadores \
  -H "Authorization: Bearer $TOKEN_JUGADOR"
```

