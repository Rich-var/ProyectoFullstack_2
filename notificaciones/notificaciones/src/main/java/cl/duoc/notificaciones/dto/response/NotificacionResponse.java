package cl.duoc.notificaciones.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificacionResponse {
    private Long idNotificacion;
    private String destinatario;
    private String mensaje;
    private String tipo;
    private String estado;
    private LocalDateTime fechaEnvio;
}