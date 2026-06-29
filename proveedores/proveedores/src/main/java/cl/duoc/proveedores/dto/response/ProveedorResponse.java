package cl.duoc.proveedores.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ProveedorResponse {

    private Long idProveedor;
    private String nombre;
    private String rubro;
    private String telefono;
    private String email;
    private String direccion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}