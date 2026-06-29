# MicroServicio Auth - JWT + Spring Security + Flyway + MySQL

Microservicio de autenticación desarrollado con Spring Boot utilizando Spring Security y JWT para implementar autenticación Stateless dentro de una arquitectura de microservicios.

Este servicio permite validar credenciales, generar tokens JWT y proteger APIs REST utilizando Spring Security.

---

# Objetivo del Proyecto

Este microservicio permite:

- Validar credenciales de usuario
- Generar tokens JWT
- Implementar autenticación Stateless
- Proteger APIs REST utilizando Spring Security
- Gestionar migraciones SQL con Flyway
- Persistir usuarios utilizando MySQL
- Aplicar validaciones con Jakarta Validation
- Implementar arquitectura en capas

---

# Tecnologías Utilizadas

| Tecnología           | Descripción                       |
| -------------------- | --------------------------------- |
| Java 21              | Lenguaje principal                |
| Spring Boot          | Framework principal               |
| Spring Security      | Seguridad y autenticación         |
| JWT                  | Tokens de autenticación           |
| Spring Web           | Desarrollo de APIs REST           |
| Spring Data JPA      | Persistencia ORM                  |
| Hibernate            | ORM                               |
| Flyway               | Migraciones SQL                   |
| MySQL                | Base de datos                     |
| Jakarta Validation   | Validaciones de DTOs              |
| Lombok               | Reducción de boilerplate          |
| Maven                | Gestión de dependencias           |
| Spring Boot DevTools | Reinicio automático en desarrollo |

---

# Dependencias Agregadas desde start.spring.io

Al crear el proyecto en:

```text
https://start.spring.io
```

se agregaron las siguientes dependencias desde la interfaz gráfica:

- Spring Web
- Spring Security
- Validation
- Spring Data JPA
- MySQL Driver
- Flyway Migration
- Lombok
- Spring Boot DevTools

---

## Spring Web

Permite:

- Crear APIs REST
- Utilizar `@RestController`
- Utilizar `@PostMapping`
- Serialización automática JSON
- Tomcat embebido

---

## Spring Security

Permite:

- Autenticación
- Autorización
- Protección de endpoints
- Filtros de seguridad
- Arquitectura Stateless
- Integración con JWT

---

## Validation

Permite:

- Validar DTOs
- Utilizar `@Valid`
- Utilizar validaciones como:
  - `@NotBlank`
  - `@NotNull`

---

## Spring Data JPA

Permite:

- Persistencia ORM
- Uso de `JpaRepository`
- Mapear entidades con `@Entity`
- Integración con Hibernate

---

## MySQL Driver

Permite:

- Conectarse a MySQL
- Utilizar JDBC
- Integrarse con Hibernate y Spring Data JPA

---

## Flyway Migration

Permite:

- Ejecutar migraciones SQL automáticamente
- Versionar scripts SQL
- Crear tablas automáticamente
- Mantener control de versiones de la base de datos

---

## Lombok

Permite:

- Reducir código repetitivo
- Generar getters y setters automáticamente
- Generar constructores automáticamente
- Simplificar entidades y DTOs

---

## Spring Boot DevTools

Permite:

- Reinicio automático
- Hot reload
- Mejor experiencia de desarrollo

---

# Dependencias Agregadas Manualmente

Las siguientes dependencias fueron agregadas manualmente al proyecto.

---

## JWT

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>4.4.0</version>
</dependency>
```

---

### ¿Qué permite?

- Crear JWT
- Firmar JWT
- Validar JWT
- Leer Claims
- Verificar expiración
- Implementar autenticación Stateless

---

## Spring Boot Configuration Processor

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

---

### ¿Qué permite?

- Metadata Spring
- Autocompletado en VS Code
- Validación de properties
- Reconocimiento automático de configuraciones personalizadas

---

# Arquitectura Stateless

El proyecto utiliza autenticación Stateless.

Esto significa:

- El servidor NO almacena sesiones HTTP
- Cada request debe enviar el JWT
- El cliente se autentica una vez
- Luego reutiliza el token en las siguientes solicitudes

Configuración utilizada:

```java
.sessionManagement(session ->
    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```

---

# Arquitectura del Proyecto

El proyecto utiliza arquitectura en capas.

```text
src/main/java/cl/duoc/auth
├── configuration      # Configuraciones y properties
├── controller         # Endpoints REST
├── dto                # DTO Request y Response
│   ├── request
│   └── response
├── model              # Entidades JPA
├── repository         # Acceso a datos
├── security           # Configuración Spring Security
├── service            # Lógica de negocio
└── AuthApplication.java
```

---

# Flujo JWT

## 1. Login

El cliente realiza una solicitud HTTP:

```http
POST /api/v1/auth/login
```

---

## 2. Validación

El sistema:

- Busca usuario en MySQL
- Valida credenciales
- Genera JWT firmado

---

## 3. Respuesta

El sistema retorna:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

## 4. Consumo de APIs protegidas

El cliente debe enviar:

```http
Authorization: Bearer TOKEN
```

---

# Endpoints Principales

---

## Login

```http
POST /api/v1/auth/login
```

---

### Ejemplo Request

```json
{
  "username": "admin",
  "password": "1234"
}
```

---

### Ejemplo Response

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

---

# Configuración Flyway

## Ruta de migraciones

```properties
spring.flyway.locations=classpath:bd/migration
```

---

## Estructura

```text
src/main/resources/bd/migration
```

---

## Scripts SQL

### V1\_\_crear_tabla_usuario.sql

Permite:

- Crear tabla usuario
- Definir estructura inicial

---

### V2\_\_insertar_usuarios_demo.sql

Permite:

- Insertar usuarios demo
- Poblar datos iniciales

---

# Usuarios de Prueba

| Username | Password | Rol   |
| -------- | -------- | ----- |
| admin    | 1234     | ADMIN |
| usuario  | 1234     | USER  |

---

# Configuración Base de Datos

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/db_usuario

spring.datasource.username=usuario
spring.datasource.password=pass123
```

---

# Configuración JWT

```properties
jwt.secret=clave-super-secreta-para-clase-123456

jwt.expiration=3600000
```

---

# Seguridad Implementada

## Spring Security

Implementa:

- Protección de endpoints
- Filtros HTTP
- Arquitectura Stateless
- Seguridad desacoplada

---

## JWT

Implementa:

- Firma HMAC256
- Bearer Token
- Claims
- Expiración
- Validación de integridad

---

# Componentes Principales

## AuthController

Gestiona:

- Endpoint login
- Solicitudes de autenticación

---

## AuthService

Gestiona:

- Validación de credenciales
- Lógica de autenticación

---

## JwtService

Gestiona:

- Creación JWT
- Validación JWT
- Lectura de claims

---

## SecurityConfig

Gestiona:

- Configuración Spring Security
- Seguridad HTTP
- Filtros de autenticación

---

# Modelo de Datos

## UsuarioModel

Representa un usuario autenticado dentro del sistema.

---

# Ejecución del Proyecto

## macOS / Linux

```bash
./mvnw spring-boot:run
```

---

## Windows

```bash
mvnw.cmd spring-boot:run
```

---

# Build del Proyecto

```bash
./mvnw clean package
```

---

# Ejecutar JAR

```bash
java -jar target/*.jar
```

---

# Conceptos Implementados

- REST API
- DTO
- JPA
- Hibernate
- JWT
- Spring Security
- Flyway
- MySQL
- Arquitectura Stateless
- Repository Pattern
- Service Layer
- Dependency Injection
- Bean Validation
- Seguridad REST
- Bearer Token
- Filtros HTTP
- Autenticación desacoplada

---

# Buenas Prácticas Implementadas

- Arquitectura en capas
- Separación de responsabilidades
- Uso de DTOs
- Validaciones con Jakarta
- Configuración por perfiles
- Uso de Lombok
- Uso de Spring Security
- Seguridad Stateless
- Migraciones versionadas con Flyway
- Persistencia desacoplada mediante Hibernate

---

# Mejoras Futuras

- Refresh Tokens
- Roles y permisos avanzados
- OAuth2
- Integración con API Gateway
- Dockerización
- Integración continua con GitHub Actions
- Swagger/OpenAPI
- Logs estructurados
- Pruebas unitarias con JUnit y Mockito
- Rate limiting
- Monitoreo con Spring Boot Actuator
- Implementación de Redis para blacklist de tokens
- Integración con otros microservicios protegidos

---

# Autor

Proyecto académico desarrollado por **Daniel Riquelme**
como parte del aprendizaje de:

- Spring Boot
- Spring Security
- JWT
- Microservicios
- Flyway
- MySQL
- Arquitectura REST
- DTOs
- Validaciones con Jakarta
- Arquitectura Stateless
