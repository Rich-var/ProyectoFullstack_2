package cl.duoc.pedidos.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> manejarPedidoNoEncontrado(
            PedidoNoEncontradoException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.NOT_FOUND.value())
                .error("Pedido no encontrado")
                .mensajes(List.of(ex.getMessage()))
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> manejarArgumentoInvalido(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.BAD_REQUEST.value())
                .error("Solicitud incorrecta")
                .mensajes(List.of(ex.getMessage()))
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarValidaciones(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<String> mensajes = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.BAD_REQUEST.value())
                .error("Error de validacion")
                .mensajes(mensajes)
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Caso especifico: el microservicio remoto (Usuarios o Productos) respondio
     * con un 404 al pedir un recurso por ID via Feign (ej: idProducto que no existe).
     * Sin este handler, esa FeignException.NotFound caia en el handler generico
     * de Exception y se devolvia un 500 sin contexto util para el cliente.
     */
    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ErrorResponse> manejarRecursoRemotoNoEncontrado(
            FeignException.NotFound ex,
            HttpServletRequest request
    ) {
        log.warn("Recurso remoto no encontrado al comunicarse con otro microservicio: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.NOT_FOUND.value())
                .error("Recurso remoto no encontrado")
                .mensajes(List.of("El usuario o producto referenciado no existe en el microservicio remoto"))
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Caso general de fallas de comunicacion remota: timeouts, conexion rechazada
     * (microservicio caido) o cualquier otro codigo de error proveniente de
     * Usuarios-Service o Productos-Service via Feign. RetryableException
     * (timeouts/conexion rechazada) tambien hereda de FeignException, por lo
     * que queda cubierta aqui.
     */
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> manejarErrorComunicacionRemota(
            FeignException ex,
            HttpServletRequest request
    ) {
        log.error("Fallo de comunicacion con un microservicio remoto (status {}): {}", ex.status(), ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Servicio remoto no disponible")
                .mensajes(List.of("No fue posible comunicarse con Usuarios-Service o Productos-Service. Intenta nuevamente más tarde."))
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Error inesperado no controlado: {}", ex.getMessage(), ex);

        ErrorResponse error = ErrorResponse.builder()
                .fecha(LocalDateTime.now())
                .estado(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Error interno del servidor")
                .mensajes(List.of("Ocurrio un error inesperado"))
                .ruta(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
