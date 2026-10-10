# Proyecto: Reserva de Canchas Deportivas

API REST de reserva de canchas deportivas con estadísticas del jugador y highlights de partidos.

## Stack

- Java 17, Spring Boot 3, Maven
- Spring Web, Spring Data JPA, Spring Validation, Spring Security + JWT
- PostgreSQL 16 (base de datos definida; no usar MySQL), Flyway para migraciones, docker-compose para levantarla
- springdoc-openapi (Swagger), Lombok, MapStruct
- JUnit 5, Mockito, Testcontainers

## Arquitectura

MVC por capas. Paquetes: `controller`, `service`, `repository`, `model` (entidades), `dto` (request/response), `exception`, `config`, `security`.

- El controlador nunca usa repositorios directamente.
- Las entidades nunca se devuelven al cliente: siempre DTO.
- Los servicios dependen de interfaces (inyección por constructor).

## Modelo de dominio

Sigue **exactamente** los diagramas UML de esta carpeta (`docs/`): clases, atributos, métodos, herencia, interfaces y multiplicidades. No inventes clases nuevas ni cambies nombres sin avisar.

- `00_general_poo.puml`: vista general
- `01_personas.puml`: Persona, Usuario, Jugador, AdministradorSede, Administrador, Invitado
- `02_canchas_tarifas.puml`: Sede, Cancha y subclases, EstrategiaTarifa, BloqueoCancha
- `03_reservas_pagos.puml`: Reserva, Pago y subclases
- `04_partidos_eventos.puml`: Partido, ParticipantePartido, EventoPartido y subclases
- `05_sensores_multimedia.puml`: Dispositivo, SensorArco, CamaraCancha, Grabacion, Clip, detectores, generador de clips, almacenamiento
- `06_enumeraciones.puml`: enumeraciones

## Reglas de negocio clave

- No puede haber dos reservas solapadas en la misma cancha: restricción de exclusión de PostgreSQL (extensión `btree_gist`, `EXCLUDE USING gist` con `tsrange` y `&&`, solo para reservas PENDIENTE y CONFIRMADA) creada en una migración Flyway, más bloqueo optimista con `@Version`.
- El precio se calcula con la `EstrategiaTarifa` de la cancha.
- Un jugador organiza la reserva; los demás participantes pueden ser Jugador registrado o Invitado (sin cuenta).
- Un Invitado puede reclamar su perfil con un token y heredar su historial.
- Los sensores de los arcos envían un evento de gol; se crea un `Gol` en estado `PENDIENTE_ATRIBUCION` y se genera un `Clip` (X segundos antes y Y después, configurables).
- Los jugadores atribuyen el gol (anotador/asistente) y otro jugador lo confirma.
- Los eventos sin atribuir se cierran automáticamente pasado el plazo (por ejemplo 48 horas).
- Las estadísticas se calculan con consultas agregadas sobre los eventos CONFIRMADOS.
- Cancelación de reservas permitida hasta 2 horas antes.
- Los sensores se autentican con `codigoSensor` + `claveApi`.
- El detector de goles es intercambiable (`IDetectorGol`): sensores ahora, YOLO más adelante.

## Convenciones

- Nombres en español para el dominio.
- Código limpio, validaciones con anotaciones.
- Manejo global de excepciones con `@RestControllerAdvice` y respuestas de error consistentes.
- Pruebas unitarias en cada fase; compilar y ejecutar `mvn clean verify` al terminar.
