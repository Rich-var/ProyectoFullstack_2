package cl.duoc.productos.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductoResponse {

    private Long idProducto;
    private String nombre;
    private String descripcion;
    private Integer precio;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}