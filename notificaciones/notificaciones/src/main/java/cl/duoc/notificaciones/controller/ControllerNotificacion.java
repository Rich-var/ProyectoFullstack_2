package cl.duoc.notificaciones.controller;

import cl.duoc.notificaciones.dto.request.NotificacionRequest;
import cl.duoc.notificaciones.dto.response.NotificacionResponse;
import cl.duoc.notificaciones.service.NotificacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
@RequiredArgsConstructor
public class ControllerNotificacion {

    private final NotificacionService notificacionService;

    @PostMapping
    public ResponseEntity<NotificacionResponse> enviarNotificacion(@Valid @RequestBody NotificacionRequest request) {
        NotificacionResponse response = notificacionService.registrarNotificacion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}