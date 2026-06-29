package cl.duoc.pedidos.dto.response;

import cl.duoc.pedidos.client.dto.ProductoDTO;
import cl.duoc.pedidos.client.dto.UsuarioDTO;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class PedidoResponse {
    private Long idPedido;
    private UsuarioDTO usuario;
    private ProductoDTO producto;
    private Integer cantidad;
    private Integer total;
    private String estado;
    private LocalDateTime fechaCreacion;
}