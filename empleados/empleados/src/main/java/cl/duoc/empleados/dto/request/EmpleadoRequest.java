package cl.duoc.empleados.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EmpleadoRequest {

    @NotBlank(message = "El RUT del empleado es obligatorio")
    @Size(min = 8, max = 12, message = "El RUT debe tener entre 8 y 12 caracteres")
    private String rut;

    @NotBlank(message = "Los nombres del empleado son obligatorios")
    @Size(min = 2, max = 80, message = "Los nombres deben tener entre 2 y 80 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos del empleado son obligatorios")
    @Size(min = 2, max = 80, message = "Los apellidos deben tener entre 2 y 80 caracteres")
    private String apellidos;

    @NotBlank(message = "El email del empleado es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    @NotBlank(message = "El telefono del empleado es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @NotBlank(message = "El cargo del empleado es obligatorio")
    @Size(min = 2, max = 60, message = "El cargo debe tener entre 2 y 60 caracteres")
    private String cargo;

    @NotNull(message = "El ID de la sucursal es obligatorio")
    private Long idSucursal;

    private Boolean activo;
}