package cl.duoc.sucursales.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class SucursalResponse {

    private Long idSucursal;
    private String nombre;
    private String direccion;
    private String comuna;
    private String telefono;
    private String horarioApertura;
    private String horarioCierre;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}