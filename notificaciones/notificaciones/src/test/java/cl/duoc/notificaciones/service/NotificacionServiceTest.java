package cl.duoc.notificaciones.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.notificaciones.dto.request.NotificacionRequest;
import cl.duoc.notificaciones.dto.response.NotificacionResponse;
import cl.duoc.notificaciones.model.NotificacionModel;
import cl.duoc.notificaciones.repository.NotificacionRepository;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private NotificacionRequest crearRequest(String destinatario, String tipo) {
        NotificacionRequest request = new NotificacionRequest();
        request.setDestinatario(destinatario);
        request.setMensaje("Tu pedido esta listo");
        request.setTipo(tipo);
        return request;
    }

    @Test // Caso: registrar notificacion exitosamente y verificar el mapeo de campos.
    void registrarNotificacion_debeGuardarYRetornarRespuestaMapeada() {
        // GIVEN.
        NotificacionRequest request = crearRequest("cliente@test.cl", "email");
        NotificacionModel guardada = NotificacionModel.builder()
                .idNotificacion(1L)
                .destinatario("cliente@test.cl")
                .mensaje("Tu pedido esta listo")
                .tipo("EMAIL")
                .estado("ENVIADO")
                .fechaEnvio(LocalDateTime.now())
                .build();
        when(notificacionRepository.save(any(NotificacionModel.class))).thenReturn(guardada);

        // WHEN.
        NotificacionResponse resultado = notificacionService.registrarNotificacion(request);

        // THEN.
        assertThat(resultado.getIdNotificacion()).isEqualTo(1L);
        assertThat(resultado.getEstado()).isEqualTo("ENVIADO");
        verify(notificacionRepository).save(any(NotificacionModel.class));
    }

    @Test // Caso: el tipo enviado en minusculas debe normalizarse a mayusculas antes de guardar.
    void registrarNotificacion_debeNormalizarTipoAMayusculas() {
        // GIVEN.
        NotificacionRequest request = crearRequest("cliente@test.cl", "sms");
        ArgumentCaptor<NotificacionModel> captor = ArgumentCaptor.forClass(NotificacionModel.class);
        when(notificacionRepository.save(any(NotificacionModel.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN.
        notificacionService.registrarNotificacion(request);

        // THEN.
        verify(notificacionRepository).save(captor.capture());
        assertThat(captor.getValue().getTipo()).isEqualTo("SMS");
    }
}
