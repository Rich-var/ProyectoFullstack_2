package cl.duoc.notificaciones.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long idNotificacion;

    @Column(nullable = false, length = 120)
    private String destinatario;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false, length = 20)
    private String tipo; // EMAIL, SMS, PUSH

    @Column(nullable = false, length = 20)
    private String estado; // ENVIADO, PENDIENTE, ERROR

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @PrePersist
    public void prePersist() {
        this.fechaEnvio = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = "ENVIADO"; // Estado por defecto simulado
        }
    }
}