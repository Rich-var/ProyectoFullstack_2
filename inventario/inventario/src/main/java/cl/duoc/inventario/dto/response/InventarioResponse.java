package cl.duoc.inventario.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InventarioResponse {
    private Long idInventario;
    private Long idProducto;
    private Long idSucursal;
    private Integer cantidad;
    private LocalDateTime fechaActualizacion;
}