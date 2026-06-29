package cl.duoc.usuarios.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UsuarioResponse {

    private Long idUsuario;
    private String username;
    private String email;
    private String rol;
    private Long idEmpleado;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
}