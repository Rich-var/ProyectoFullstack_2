package cl.duoc.usuarios.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequest {

    @NotBlank(message = "El username es obligatorio")
    @Size(min = 3, max = 80, message = "El username debe tener entre 3 y 80 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato valido")
    @Size(max = 120, message = "El email no puede superar los 120 caracteres")
    private String email;

    @NotBlank(message = "La password es obligatoria")
    @Size(min = 4, max = 120, message = "La password debe tener entre 4 y 120 caracteres")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    @Size(min = 3, max = 50, message = "El rol debe tener entre 3 y 50 caracteres")
    private String rol;

    private Long idEmpleado;

    private Boolean activo;
}