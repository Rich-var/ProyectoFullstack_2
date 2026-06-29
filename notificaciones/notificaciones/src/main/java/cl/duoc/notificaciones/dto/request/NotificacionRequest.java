package cl.duoc.notificaciones.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NotificacionRequest {

    @NotBlank(message = "El destinatario es obligatorio")
    @Size(min = 3, max = 120, message = "El destinatario debe tener entre 3 y 120 caracteres")
    private String destinatario;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 500, message = "El mensaje no puede superar los 500 caracteres")
    private String mensaje;

    @NotBlank(message = "El tipo de notificación (EMAIL/SMS/PUSH) es obligatorio")
    private String tipo;
}