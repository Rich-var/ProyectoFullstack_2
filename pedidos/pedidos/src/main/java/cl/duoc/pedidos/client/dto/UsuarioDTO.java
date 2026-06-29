package cl.duoc.pedidos.client.dto;

import lombok.Data;

@Data
public class UsuarioDTO {
    private Long idUsuario;
    private String username;
    private String email;
    private Boolean activo;
}