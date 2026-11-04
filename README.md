# cliente-service

## Descripción

`cliente-service` es un microservicio desarrollado para la plataforma financiera FinCore Manager de FinCore Solutions S.A.S. Su objetivo es gestionar la información de clientes dentro de una arquitectura basada en microservicios, permitiendo una migración gradual desde un sistema monolítico legacy hacia un entorno moderno, escalable y automatizado bajo prácticas DevOps.

El servicio administra operaciones relacionadas con clientes, incluyendo creación, consulta, actualización y gestión de estado, además de integrarse con otros servicios mediante eventos Kafka y APIs REST seguras.

---

## Tecnologías utilizadas

- Java 21
- Spring Boot 3
- Spring Security
- Keycloak JWT
- PostgreSQL
- Flyway
- Apache Kafka
- Docker
- Jenkins
- SonarQube
- JUnit 5
- Mockito
- Testcontainers

---
# Funcionalidades principales

- Registro de clientes
- Consulta de clientes
- Actualización de información
- Cambio de estado de cliente
- Validaciones de negocio
- Seguridad JWT
- Auditoría básica
- Publicación de eventos Kafka
- Health checks
- Métricas del sistema

---

# Estructura del proyecto

```text
cliente-service/
├── src/
│
├── main/
│   ├── java/
│   │   └── ban/
│   │       └── fincore/
│   │           └── client/
│   │
│   │               ├── config/
│   │               ├── controller/
│   │               ├── dto/
│   │               ├── entity/
│   │               ├── enums/
│   │               ├── event/
│   │               ├── exception/
│   │               ├── mapper/
│   │               ├── repository/
│   │               ├── security/
│   │               ├── service/
│   │               │   └── impl/
│   │               └── ClienteServiceApplication.java
│   │
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── application-test.yml
│       └── db/
│           └── migration/
│               └── V1__create_client_schema.sql
│
├── test/
│   └── java/
│       └── ban/
│           └── fincore/
│               └── client/
│                   ├── controller/
│                   ├── integration/
│                   └── service/
│
├── docker/
│   ├── kafka/
│   └── postgres/
│
├── Dockerfile
├── docker-compose.yml
├── Jenkinsfile
├── pom.xml
├── README.md
└── .gitignore
```

---

# Variables de entorno

## Base de datos

| Variable | Descripción |
|---|---|
| DB_URL | URL conexión PostgreSQL |
| DB_USERNAME | Usuario PostgreSQL |
| DB_PASSWORD | Contraseña PostgreSQL |

---

## Kafka

| Variable | Descripción |
|---|---|
| KAFKA_BOOTSTRAP_SERVERS | Servidor Kafka |

---

## Seguridad

| Variable | Descripción |
|---|---|
| KEYCLOAK_ISSUER_URI | URL issuer Keycloak |
| KEYCLOAK_REALM | Realm |
| KEYCLOAK_CLIENT_ID | Cliente |

---

# Configuración local

## Clonar repositorio

```bash
git clone https://github.com/empresa/cliente-service.git
```

---

## Ingresar al proyecto

```bash
cd cliente-service
```

---

# Compilación

```bash
./mvnw clean install
```

---

# Ejecución local

```bash
./mvnw spring-boot:run
```

---

# Levantar infraestructura Docker

```bash
docker compose up -d
```

---

# Endpoints principales

| Método | Endpoint | Descripción |
|---|---|---|
| POST | /api/v1/clients | Crear cliente |
| GET | /api/v1/clients | Listar clientes |
| GET | /api/v1/clients/{id} | Consultar cliente |
| PUT | /api/v1/clients/{id} | Actualizar cliente |
| PATCH | /api/v1/clients/{id}/status | Cambiar estado |
| DELETE | /api/v1/clients/{id} | Eliminación lógica |

---

# Seguridad

El sistema utiliza autenticación y autorización mediante JWT con Keycloak.

## Roles

| Rol | Descripción |
|---|---|
| ROLE_ADMIN | Administración total |
| ROLE_USER | Operaciones básicas |
| ROLE_AUDITOR | Solo lectura |

---

# Base de datos

El servicio utiliza PostgreSQL como motor principal.

Las migraciones son administradas mediante Flyway:

```text
src/main/resources/db/migration
```

Ejemplo:

```text
V1__create_client_schema.sql
```

---

# Eventos Kafka

## Tópicos

| Topic | Descripción |
|---|---|
| client.created | Cliente creado |
| client.updated | Cliente actualizado |
| client.status-changed | Cambio estado cliente |

---

# Integración CI/CD

El proyecto implementa integración continua mediante Jenkins.

## Pipeline

El pipeline ejecuta:

- Checkout código
- Compilación
- Pruebas unitarias
- Pruebas integración
- SonarQube
- Build Docker
- Push Docker Hub

---

# Calidad de código

Se utiliza SonarQube para:

- Code smells
- Coverage
- Vulnerabilidades
- Bugs
- Duplicación

---

# Pruebas

## Pruebas unitarias

Tecnologías:

- JUnit 5
- Mockito

Ubicación:

```text
src/test/java/.../service
```

---

## Pruebas de integración

Tecnologías:

- Testcontainers
- PostgreSQL Container

Ubicación:

```text
src/test/java/.../integration
```

---

# Observabilidad

El proyecto implementa Spring Boot Actuator.

## Endpoints habilitados

| Endpoint | Descripción |
|---|---|
| /actuator/health | Estado aplicación |
| /actuator/info | Información |
| /actuator/metrics | Métricas |
| /actuator/prometheus | Métricas Prometheus |

---

# Docker

## Construcción imagen

```bash
docker build -t cliente-service .
```

---

## Ejecución contenedor

```bash
docker run -p 8081:8081 cliente-service
```

---

# Integración con otros servicios

| Servicio | Tipo integración |
|---|---|
| auth-service | JWT |
| api-gateway | Routing |
| credit-service | REST/Kafka |
| payment-service | REST |
| notification-service | Kafka |
| report-service | Kafka |

---

# Flujo DevOps implementado

```text
Developer
   ↓
GitHub
   ↓
Jenkins Pipeline
   ↓
Tests
   ↓
SonarQube
   ↓
Docker Build
   ↓
Docker Registry
   ↓
Deploy
```

---

# Roadmap futuro

- API Gateway centralizado
- Kubernetes
- Helm Charts
- OpenTelemetry
- Grafana
- Prometheus
- ELK Stack
- Config Server
- Service Discovery
- Resilience4J
- Circuit Breaker

---

# Estado actual

El proyecto se encuentra en fase inicial de implementación dentro de la estrategia de migración evolutiva desde arquitectura monolítica hacia microservicios orientados a prácticas DevOps empresariales.

---

# Autor

Proyecto desarrollado para:

**FinCore Solutions S.A.S.**

Arquitectura orientada a microservicios y automatización DevOps empresarial.