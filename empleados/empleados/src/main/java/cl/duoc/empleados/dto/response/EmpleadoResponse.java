package cl.duoc.empleados.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmpleadoResponse {

    private Long idEmpleado;
    private String rut;
    private String nombres;
    private String apellidos;
    private String email;
    private String telefono;
    private String cargo;
    private Long idSucursal;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}