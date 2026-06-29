package cl.duoc.proveedores.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorRequest {

    @NotBlank(message = "El nombre del proveedor es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "El rubro del proveedor es obligatorio")
    @Size(min = 2, max = 100, message = "El rubro debe tener entre 2 y 100 caracteres")
    private String rubro;

    @NotBlank(message = "El telefono del proveedor es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @NotBlank(message = "El email del proveedor es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    @Size(max = 255, message = "La direccion no puede superar los 255 caracteres")
    private String direccion;

    private Boolean activo;
}