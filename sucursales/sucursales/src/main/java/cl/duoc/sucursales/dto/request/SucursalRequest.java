package cl.duoc.sucursales.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SucursalRequest {

    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(min = 2, max = 120, message = "El nombre debe tener entre 2 y 120 caracteres")
    private String nombre;

    @NotBlank(message = "La direccion de la sucursal es obligatoria")
    @Size(min = 5, max = 255, message = "La direccion debe tener entre 5 y 255 caracteres")
    private String direccion;

    @NotBlank(message = "La comuna de la sucursal es obligatoria")
    @Size(min = 2, max = 80, message = "La comuna debe tener entre 2 y 80 caracteres")
    private String comuna;

    @NotBlank(message = "El telefono de la sucursal es obligatorio")
    @Pattern(regexp = "^[0-9+\\- ]{8,20}$", message = "El telefono debe tener un formato valido")
    private String telefono;

    @NotBlank(message = "El horario de apertura es obligatorio")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "El horario de apertura debe tener formato HH:mm")
    private String horarioApertura;

    @NotBlank(message = "El horario de cierre es obligatorio")
    @Pattern(regexp = "^([01]\\d|2[0-3]):[0-5]\\d$", message = "El horario de cierre debe tener formato HH:mm")
    private String horarioCierre;

    private Boolean activo;
}