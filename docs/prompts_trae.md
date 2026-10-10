# Prompts para Trae - Proyecto Reserva de Canchas

## Preparación (antes de escribir ningún prompt)

1. Crea una carpeta vacía del proyecto y ábrela en Trae.
2. Dentro crea una carpeta `docs/` y copia ahí los archivos `.puml` (carpeta `uml_poo`).
3. Crea `docs/REQUISITOS.md` con el texto del bloque "Contexto del proyecto" (abajo).
4. Trabaja **una fase a la vez**. Después de cada fase: compila, prueba, haz `git commit` y recién pasa a la siguiente.

---

## Contexto del proyecto (pegar en `docs/REQUISITOS.md` y en el primer prompt)

```
Proyecto: API REST de reserva de canchas deportivas con estadísticas del jugador y highlights de partidos.

Stack:
- Java 17, Spring Boot 3, Maven
- Spring Web, Spring Data JPA, Spring Validation, Spring Security + JWT
- PostgreSQL 16 (base de datos definida; no usar MySQL), Flyway para migraciones, docker-compose para levantarla
- springdoc-openapi (Swagger), Lombok, MapStruct
- JUnit 5 + Mockito + Testcontainers

Arquitectura: MVC por capas, paquetes: controller, service, repository, model (entidades), dto (request/response), exception, config, security.
- El controlador nunca usa repositorios directamente.
- Las entidades nunca se devuelven al cliente: siempre DTO.
- Los servicios dependen de interfaces (inyección por constructor).

Modelo de dominio: sigue EXACTAMENTE los diagramas UML de la carpeta docs/ (clases, atributos, métodos, herencia, interfaces y multiplicidades). No inventes clases nuevas ni cambies nombres sin avisarme.

Reglas de negocio clave:
- No puede haber dos reservas solapadas en la misma cancha: restricción de exclusión de PostgreSQL (extensión btree_gist, EXCLUDE USING gist con tsrange y &&, solo para reservas PENDIENTE y CONFIRMADA) creada en una migración Flyway, más bloqueo optimista con @Version.
- El precio se calcula con la EstrategiaTarifa de la cancha.
- Un jugador organiza la reserva; los demás participantes pueden ser Jugador registrado o Invitado (sin cuenta).
- Un Invitado puede reclamar su perfil con un token y heredar su historial.
- Los sensores de los arcos envían un evento de gol; se crea un Gol en estado PENDIENTE_ATRIBUCION y se genera un Clip (X segundos antes y Y después, configurables).
- Los jugadores atribuyen el gol (anotador/asistente) y otro jugador lo confirma.
- Las estadísticas se calculan con consultas agregadas sobre los eventos CONFIRMADOS.

Convenciones: nombres en español para el dominio, código limpio, validaciones con anotaciones, manejo global de excepciones con @RestControllerAdvice, respuestas de error consistentes.
```

---

## Fase 0 - Esqueleto del proyecto

```
Lee docs/REQUISITOS.md y los diagramas UML de docs/.
Crea el proyecto Spring Boot con Maven, la estructura de paquetes (controller, service, repository, model, dto, exception, config, security), la configuración de application.yml con perfiles dev y test, Flyway, Swagger y un @RestControllerAdvice básico.
No implementes entidades todavía. Al terminar compila con mvn clean verify y dime qué falta.
```

## Fase 1 - Personas y seguridad

```
Implementa el módulo de personas según docs/01_personas.puml:
- Persona (abstracta), Usuario, Jugador, AdministradorSede, Administrador, Invitado.
- Usa herencia JPA con @Inheritance(strategy = JOINED) y respeta los atributos y métodos del diagrama.
- Repositorio, servicio, controlador y DTO para registro y login.
- Seguridad con Spring Security + JWT (registro, login, rutas protegidas por rol).
- Migración Flyway para las tablas.
- Pruebas unitarias del servicio de autenticación.
Compila y ejecuta las pruebas al terminar.
```

## Fase 2 - Sedes, canchas y tarifas

```
Implementa docs/02_canchas_tarifas.puml:
- Sede, Cancha (abstracta con herencia JOINED) y sus subclases, BloqueoCancha.
- EstrategiaTarifa (interfaz) con TarifaEstandar, TarifaHoraPico y TarifaPromocional (decorador). Guarda la estrategia elegida en la cancha (por ejemplo con un tipo y sus parámetros) y resuélvela con una fábrica.
- Endpoints: CRUD de sedes y canchas (solo AdministradorSede/Administrador) y GET /api/canchas/{id}/disponibilidad?fecha=.
- Pruebas unitarias de cada estrategia de tarifa.
```

## Fase 3 - Reservas y pagos

```
Implementa docs/03_reservas_pagos.puml:
- Reserva con @Version y restricción única que evite solapamientos.
- Pago (abstracta, JOINED) con PagoTarjeta, PagoBilletera, PagoTransferencia, PagoEfectivo; procesar() simulado.
- Endpoints: crear, listar mis reservas, cancelar, reprogramar, pagar.
- Regla de cancelación hasta 2 horas antes.
- Pruebas: doble reserva simultánea debe fallar, cálculo de monto, cancelación fuera de plazo.
```

## Fase 4 - Partidos, eventos y estadísticas

```
Implementa docs/04_partidos_eventos.puml:
- Partido, ParticipantePartido (referencia a Persona), EventoPartido (abstracta) con Gol, GolEnContra, Tarjeta, TarjetaAmarilla, TarjetaRoja.
- Enlace de invitación por token y reclamo de perfil de Invitado.
- Atribución y confirmación de goles; tarea @Scheduled que cierra eventos vencidos.
- EstadisticaService con consultas agregadas (JPQL) y endpoints GET /api/jugadores/{id}/estadisticas y GET /api/rankings.
- Pruebas del cálculo de estadísticas y del marcador (recalcularMarcador usando afectaMarcador()).
```

## Fase 5 - Sensores, grabaciones y clips

```
Implementa docs/05_sensores_multimedia.puml:
- Dispositivo (abstracta), SensorArco, CamaraCancha, Grabacion, Clip.
- Interfaces IDetectorGol (DetectorPorSensor), IGeneradorClip (GeneradorClipFFmpeg) e IAlmacenamiento (AlmacenamientoLocal por ahora; deja AlmacenamientoS3 como esqueleto).
- Endpoint POST /api/sensores/eventos protegido con codigoSensor + claveApi: crea el Gol pendiente y dispara la generación del clip de forma asíncrona (@Async).
- Para pruebas, el generador de clips debe poder simularse sin FFmpeg instalado.
- Deja DetectorPorYolo como clase esqueleto sin implementar.
- Endpoints para listar y descartar clips de un partido.
```

## Fase 6 - Cierre

```
Revisa todo el proyecto:
1. Que las clases coincidan con los diagramas UML de docs/ y dime las diferencias.
2. Que no haya lógica de negocio en los controladores.
3. Agrega documentación Swagger a todos los endpoints.
4. Escribe un README con cómo ejecutar el proyecto, variables de entorno y ejemplos de peticiones.
5. Agrega un docker-compose con la base de datos.
6. Ejecuta mvn clean verify y corrige lo que falle.
```

---

## Prompts de corrección útiles

- "Compila y ejecuta las pruebas; corrige los errores sin cambiar el diseño de los diagramas."
- "Explícame por qué elegiste esta solución y qué alternativas había."
- "Muéstrame solo los archivos que modificaste en esta fase."
- "No toques los archivos de otras fases."
