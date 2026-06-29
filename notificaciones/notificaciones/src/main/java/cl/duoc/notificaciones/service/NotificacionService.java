package cl.duoc.notificaciones.service;

import cl.duoc.notificaciones.dto.request.NotificacionRequest;
import cl.duoc.notificaciones.dto.response.NotificacionResponse;
import cl.duoc.notificaciones.model.NotificacionModel;
import cl.duoc.notificaciones.repository.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionResponse registrarNotificacion(NotificacionRequest request) {
        log.info("Procesando envío de notificación tipo [{}] para: {}", request.getTipo(), request.getDestinatario());

        NotificacionModel notificacion = NotificacionModel.builder()
                .destinatario(request.getDestinatario())
                .mensaje(request.getMensaje())
                .tipo(request.getTipo().toUpperCase())
                .build();

        NotificacionModel guardada = notificacionRepository.save(notificacion);
        log.info("Notificación registrada con éxito. ID: {}", guardada.getIdNotificacion());

        return mapearAResponse(guardada);
    }

    private NotificacionResponse mapearAResponse(NotificacionModel model) {
        return NotificacionResponse.builder()
                .idNotificacion(model.getIdNotificacion())
                .destinatario(model.getDestinatario())
                .mensaje(model.getMensaje())
                .tipo(model.getTipo())
                .estado(model.getEstado())
                .fechaEnvio(model.getFechaEnvio())
                .build();
    }
}