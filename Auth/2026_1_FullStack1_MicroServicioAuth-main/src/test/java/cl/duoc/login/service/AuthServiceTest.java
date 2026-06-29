package cl.duoc.login.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.model.UsuarioModel;
import cl.duoc.login.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class) // Pruebas unitarias puras: no se levanta contexto de Spring ni JPA real.
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository; // Se simula el acceso a la base de datos de usuarios.

    @Mock
    private JwtService jwtService; // Se simula la generacion de JWT: aqui no se prueba la firma real del token.

    @InjectMocks
    private AuthService authService; // Clase real bajo prueba.

    private UsuarioModel crearUsuario(String username, String password, String role, boolean enabled) {
        return new UsuarioModel(1L, username, password, role, enabled);
    }

    @Test // Caso feliz: usuario existe, esta habilitado y la password coincide.
    void login_debeRetornarTokenCuandoCredencialesSonValidas() {
        // GIVEN.
        DtoAuthRequest request = new DtoAuthRequest("admin", "password123");
        UsuarioModel usuario = crearUsuario("admin", "password123", "ADMIN", true);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));
        when(jwtService.generarToken("admin", "ADMIN")).thenReturn("token-simulado-123");

        // WHEN.
        DtoAuthResponse resultado = authService.login(request);

        // THEN.
        assertThat(resultado.getToken()).isEqualTo("token-simulado-123");
        verify(jwtService).generarToken("admin", "ADMIN");
    }

    @Test // Caso de error: el username no existe en la base de datos.
    void login_debeLanzarExcepcion401CuandoUsuarioNoExiste() {
        // GIVEN.
        DtoAuthRequest request = new DtoAuthRequest("desconocido", "password123");
        when(usuarioRepository.findByUsername("desconocido")).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
        verify(jwtService, never()).generarToken(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test // Caso de error: el usuario existe pero esta deshabilitado.
    void login_debeLanzarExcepcion401CuandoUsuarioEstaDeshabilitado() {
        // GIVEN.
        DtoAuthRequest request = new DtoAuthRequest("admin", "password123");
        UsuarioModel usuario = crearUsuario("admin", "password123", "ADMIN", false);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // WHEN + THEN.
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("deshabilitado");
    }

    @Test // Caso de error: la password no coincide con la almacenada.
    void login_debeLanzarExcepcion401CuandoPasswordEsIncorrecta() {
        // GIVEN.
        DtoAuthRequest request = new DtoAuthRequest("admin", "password-equivocada");
        UsuarioModel usuario = crearUsuario("admin", "password123", "ADMIN", true);
        when(usuarioRepository.findByUsername("admin")).thenReturn(Optional.of(usuario));

        // WHEN + THEN.
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }
}
