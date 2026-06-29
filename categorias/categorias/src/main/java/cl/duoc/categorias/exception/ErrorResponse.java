package cl.duoc.categorias.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ErrorResponse {

    private LocalDateTime fecha;
    private Integer estado;
    private String error;
    private List<String> mensajes;
    private String ruta;
}