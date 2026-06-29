# Cafetería — Arquitectura de Microservicios

Proyecto semestral de **Desarrollo FullStack 1 (DSY1103)**. Arquitectura distribuida basada en
microservicios independientes para la gestión de una cafetería: usuarios, productos, categorías,
inventario, pedidos, empleados, sucursales, proveedores y notificaciones, con autenticación
centralizada y enrutamiento mediante API Gateway.

## Estudiantes

- Lillo Sebastián Vargas Ricardo

## Arquitectura general

```
Cliente / Postman
        |
        v
  API GATEWAY  :8080  (Spring Cloud Gateway MVC)
        |
        +--> AUTH-SERVICE          :8083   /api/v1/auth/**
        +--> CATEGORIAS-SERVICE    :8088   /api/categorias/**
        +--> EMPLEADOS-SERVICE     :8085   /api/empleados/**
        +--> INVENTARIO-SERVICE    :8084   /api/inventario/**
        +--> NOTIFICACIONES-SERVICE:8087   /api/notificaciones/**
        +--> PEDIDOS-SERVICE       :8095   /api/pedidos/**          (Feign -> Productos, Usuarios)
        +--> PRODUCTOS-SERVICE     :8090   /api/productos/**
        +--> PROVEEDORES-SERVICE   :8888   /api/proveedores/**
        +--> SUCURSALES-SERVICE    :8089   /api/sucursales/**
        +--> USUARIOS-SERVICE      :8086   /api/usuarios/**
        |
        v
  EUREKA SERVER :8761  (Service Discovery)
```

Cada microservicio tiene su propia base de datos MySQL (un esquema por servicio) y se registra en
Eureka para que el Gateway pueda resolverlo por nombre lógico (`lb://NOMBRE-SERVICIO`).

## Microservicios implementados (10)

| Microservicio | Puerto local | Base de datos | Persistencia |
|---|---|---|---|
| Auth (login/JWT) | 8083 | db_usuario | Flyway |
| Categorías | 8088 | db_categorias | Flyway |
| Empleados | 8085 | db_empleados | Flyway |
| Inventario | 8084 | db_inventario | Flyway |
| Notificaciones | 8087 | db_notificaciones | Flyway |
| Productos | 8090 | db_productos | Flyway |
| Proveedores | 8888 | db_proveedores | Flyway |
| Sucursales | 8089 | db_sucursales | Flyway |
| Usuarios | 8086 | db_usuarios | Flyway |
| Pedidos | 8095 | db_pedidos | Flyway |

Infraestructura: **Eureka Server** (8761) y **API Gateway** (8080).

## Funcionalidades implementadas

- CRUD completo con DTOs de request/response separados de las entidades JPA.
- Validaciones con Bean Validation (`@NotBlank`, `@Email`, `@Pattern`, etc.) y manejo centralizado
  de errores con `@RestControllerAdvice` (`GlobalExceptionHandler` + `ErrorResponse`) en los **10
  microservicios** (incluyendo `pedidos`, que además traduce errores remotos de Feign —
  `FeignException.NotFound` → 404, timeouts/conexión rechazada → 503 — en vez de un 500 genérico).
- Autenticación JWT: `AUTH-SERVICE` emite el token; el resto de los microservicios lo valida con un
  `JwtAuthenticationFilter` (Swagger, Actuator y `/api/v1/auth/login` quedan públicos).
- Comunicación entre microservicios con **Feign Client**: `pedidos-service` consulta
  `usuarios-service` y `productos-service` para validar el pedido y calcular el total, reenviando
  el header `Authorization` recibido (`FeignClientConfig`). Endpoints de `pedidos`:
  `POST /api/pedidos`, `GET /api/pedidos`, `GET /api/pedidos/{id}`,
  `GET /api/pedidos/usuario/{idUsuario}`, `PATCH /api/pedidos/{id}/estado`,
  `DELETE /api/pedidos/{id}`.
- **API Gateway** con rutas semánticas hacia los 9 servicios de negocio + filtros: un
  `default-filter` (`AddResponseHeader`) que marca toda respuesta con `X-Gateway-Origen` para
  comprobar que pasó por el Gateway, y un `AddRequestHeader` por ruta (`X-Gateway-Route`) que
  identifica qué microservicio atendió la solicitud.
- Documentación interactiva con **Swagger/OpenAPI** en `/doc/swagger-ui.html` de cada servicio,
  con esquema de seguridad JWT (`OpenApiConfig` / `SwaggerConfig`): botón **Authorize** para pegar
  el token y poder probar los endpoints protegidos directo desde la UI (pegar solo el token, sin el
  prefijo `Bearer `).
- Logs estructurados con SLF4J en la capa de servicio.
- Pruebas unitarias con **JUnit 5 + Mockito** en la capa de servicio de los 10 microservicios.
- Persistencia con **Flyway** en los 10 microservicios: scripts SQL versionados en
  `src/main/resources/db/migration/`. En perfil `dev` y `docker` se usa `baseline-on-migrate=true`
  para facilitar la primera ejecución; en `prod` se usa `ddl-auto=validate` para mayor seguridad.
- Despliegue containerizado con **Docker** (build multi-stage Maven + JRE 21) y orquestación con
  **Docker Compose**.

## Rutas principales del Gateway

| Ruta | Microservicio | Puerto |
|---|---|---|
| `/api/v1/auth/**` | auth-service | 8083 |
| `/api/productos/**` | productos-service | 8090 |
| `/api/categorias/**` | categorias-service | 8088 |
| `/api/empleados/**` | empleados-service | 8085 |
| `/api/inventario/**` | inventario-service | 8084 |
| `/api/notificaciones/**` | notificaciones-service | 8087 |
| `/api/usuarios/**` | usuarios-service | 8086 |
| `/api/pedidos/**` | pedidos-service | 8095 |
| `/api/proveedores/**` | proveedores-service | 8888 |
| `/api/sucursales/**` | sucursales-service | 8089 |

## Documentación Swagger (local)

Cada servicio expone su UI en `http://localhost:<puerto>/doc/swagger-ui.html`.
Para probar endpoints protegidos: hacer login primero (`POST /api/v1/auth/login`), copiar el token
JWT de la respuesta y pegarlo en el botón **Authorize** de Swagger (sin el prefijo `Bearer `).

| Servicio | URL Swagger local |
|---|---|
| Auth | http://localhost:8083/doc/swagger-ui.html |
| Productos | http://localhost:8090/doc/swagger-ui.html |
| Categorías | http://localhost:8088/doc/swagger-ui.html |
| Empleados | http://localhost:8085/doc/swagger-ui.html |
| Inventario | http://localhost:8084/doc/swagger-ui.html |
| Notificaciones | http://localhost:8087/doc/swagger-ui.html |
| Usuarios | http://localhost:8086/doc/swagger-ui.html |
| Pedidos | http://localhost:8095/doc/swagger-ui.html |
| Proveedores | http://localhost:8888/doc/swagger-ui.html |
| Sucursales | http://localhost:8089/doc/swagger-ui.html |

## Cómo ejecutar en local (IDE, sin Docker)

1. Tener un MySQL local corriendo en `localhost:3306` con usuario `root` y contraseña `system`.
2. Levantar en este orden:
   1. `eureka-server` (puerto 8761)
   2. `Auth` y el resto de los microservicios de negocio (cualquier orden)
   3. `api-gateway` (puerto 8080)
3. Los perfiles `dev` crean la base de datos automáticamente (`createDatabaseIfNotExist=true`) y
   Flyway aplica las migraciones en el primer arranque.
4. Probar vía Gateway: `http://localhost:8080/api/v1/auth/login`, etc.

## Cómo ejecutar con Docker

```bash
# Desde la carpeta raíz del proyecto (donde está docker-compose.yml)
docker compose up --build
```

Esto levanta los 12 contenedores (MySQL, Eureka, API Gateway, Auth y los 9 microservicios de
negocio) en la red `auth-network`, crea automáticamente todas las bases de datos
(`mysql/init/01-create-databases.sql`) y conecta cada servicio usando el perfil `docker`.

- Eureka: http://localhost:8761
- API Gateway: http://localhost:8080
- Swagger de cada servicio: `http://localhost:<puerto>/doc/swagger-ui.html`

Variables de entorno (archivo `.env` en la raíz):

| Variable | Valor para pruebas |
|---|---|
| `MYSQL_ROOT_PASSWORD` | `system` |
| `JWT_SECRET` | `clave-super-secreta-para-clase-123456` |

## Perfiles de configuración (dev / docker / prod)

Cada microservicio tiene 3 perfiles de Spring, además del `application.yml` base:

| Perfil | Archivo | Cuándo se usa | Cómo se activa |
|---|---|---|---|
| `dev` (default) | `application-dev.properties` | Desarrollo local en tu máquina, sin Docker | Automático (es el perfil por defecto) |
| `docker` | `application-docker.properties` | `docker compose up` en tu equipo | `SPRING_PROFILES_ACTIVE=docker` (ya seteado en `docker-compose.yml`) |
| `prod` | `application-prod.properties` | Despliegue en Railway / Render | `SPRING_PROFILES_ACTIVE=prod` |

## Pruebas unitarias

Cada microservicio tiene pruebas unitarias en `src/test/java/.../service/` con JUnit 5 + Mockito
y estructura Given–When–Then.

```bash
# Desde la carpeta de cada microservicio
./mvnw clean test
```

## Pruebas de integración (Postman)

En la raíz del proyecto: `Cafeteria-Microservicios.postman_collection.json`. Cubre, vía Gateway
(`http://localhost:8080`):

1. **Auth**: login (guarda el JWT automáticamente en la variable de colección `{{token}}`).
2. **Productos** y **Usuarios**: CRUD completo.
3. **Pedidos**: flujo completo (crear → listar → buscar por ID → listar por usuario →
   cambiar estado → eliminar) y 2 casos de error: usuario inactivo (`400`) y pedido inexistente
   (`404`), ambos con `ErrorResponse` estructurado.

Para usarla: importar el JSON en Postman, correr "Login" primero, y revisar **Test Results**.
