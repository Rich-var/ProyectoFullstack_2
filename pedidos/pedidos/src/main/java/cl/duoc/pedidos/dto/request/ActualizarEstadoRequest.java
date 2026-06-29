package cl.duoc.pedidos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ActualizarEstadoRequest {

    @NotBlank(message = "El nuevo estado es obligatorio")
    @Pattern(
            regexp = "PENDIENTE|PREPARANDO|ENTREGADO|CANCELADO",
            message = "El estado debe ser uno de: PENDIENTE, PREPARANDO, ENTREGADO, CANCELADO"
    )
    private String estado;
}
