package cl.duoc.usuarios.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.usuarios.dto.request.UsuarioRequest;
import cl.duoc.usuarios.dto.response.UsuarioResponse;
import cl.duoc.usuarios.exception.UsuarioNoEncontradoException;
import cl.duoc.usuarios.model.UsuarioModel;
import cl.duoc.usuarios.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UsuarioModel crearUsuarioModel(Long id, String username, String email, boolean activo) {
        return UsuarioModel.builder()
                .idUsuario(id)
                .username(username)
                .email(email)
                .password("clave1234")
                .rol("VENDEDOR")
                .idEmpleado(1L)
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private UsuarioRequest crearRequest(String username, String email) {
        UsuarioRequest request = new UsuarioRequest();
        request.setUsername(username);
        request.setEmail(email);
        request.setPassword("clave1234");
        request.setRol("VENDEDOR");
        request.setIdEmpleado(1L);
        request.setActivo(true);
        return request;
    }

    @Test
    void listarUsuarios_debeRetornarTodos() {
        // GIVEN.
        when(usuarioRepository.findAll())
                .thenReturn(List.of(crearUsuarioModel(1L, "jperez", "jperez@test.cl", true)));
        // WHEN.
        List<UsuarioResponse> resultado = usuarioService.listarUsuarios();
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarUsuariosActivos_debeFiltrarPorActivo() {
        // GIVEN.
        when(usuarioRepository.findByActivoTrue())
                .thenReturn(List.of(crearUsuarioModel(1L, "jperez", "jperez@test.cl", true)));
        // WHEN.
        List<UsuarioResponse> resultado = usuarioService.listarUsuariosActivos();
        // THEN.
        assertThat(resultado).allMatch(UsuarioResponse::getActivo);
    }

    @Test
    void listarPorRol_debeRetornarUsuariosDelRol() {
        // GIVEN.
        when(usuarioRepository.findByRolIgnoreCase("VENDEDOR"))
                .thenReturn(List.of(crearUsuarioModel(1L, "jperez", "jperez@test.cl", true)));
        // WHEN.
        List<UsuarioResponse> resultado = usuarioService.listarPorRol("VENDEDOR");
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_debeRetornarUsuarioCuandoExiste() {
        // GIVEN.
        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(crearUsuarioModel(1L, "jperez", "jperez@test.cl", true)));
        // WHEN.
        UsuarioResponse resultado = usuarioService.buscarPorId(1L);
        // THEN.
        assertThat(resultado.getUsername()).isEqualTo("jperez");
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> usuarioService.buscarPorId(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void crearUsuario_debeCrearCuandoUsernameYEmailNoExisten() {
        // GIVEN.
        UsuarioRequest request = crearRequest("jperez", "jperez@test.cl");
        when(usuarioRepository.findByUsernameIgnoreCase("jperez")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmailIgnoreCase("jperez@test.cl")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(UsuarioModel.class)))
                .thenReturn(crearUsuarioModel(1L, "jperez", "jperez@test.cl", true));
        // WHEN.
        UsuarioResponse resultado = usuarioService.crearUsuario(request);
        // THEN.
        assertThat(resultado.getIdUsuario()).isEqualTo(1L);
    }

    @Test
    void crearUsuario_debeLanzarExcepcionCuandoUsernameDuplicado() {
        // GIVEN.
        UsuarioRequest request = crearRequest("jperez", "nuevo@test.cl");
        when(usuarioRepository.findByUsernameIgnoreCase("jperez"))
                .thenReturn(Optional.of(crearUsuarioModel(5L, "jperez", "otro@test.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> usuarioService.crearUsuario(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void crearUsuario_debeLanzarExcepcionCuandoEmailDuplicado() {
        // GIVEN.
        UsuarioRequest request = crearRequest("nuevoUser", "jperez@test.cl");
        when(usuarioRepository.findByUsernameIgnoreCase("nuevoUser")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmailIgnoreCase("jperez@test.cl"))
                .thenReturn(Optional.of(crearUsuarioModel(5L, "otroUser", "jperez@test.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> usuarioService.crearUsuario(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(usuarioRepository, never()).save(any(UsuarioModel.class));
    }

    @Test
    void actualizarUsuario_debeActualizarCuandoExiste() {
        // GIVEN.
        UsuarioModel existente = crearUsuarioModel(1L, "jperez", "jperez@test.cl", true);
        UsuarioRequest request = crearRequest("jperez", "jperez@test.cl");
        request.setRol("ADMIN");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByUsernameIgnoreCase("jperez")).thenReturn(Optional.of(existente));
        when(usuarioRepository.findByEmailIgnoreCase("jperez@test.cl")).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(existente);
        // WHEN.
        UsuarioResponse resultado = usuarioService.actualizarUsuario(1L, request);
        // THEN.
        assertThat(resultado.getRol()).isEqualTo("ADMIN");
    }

    @Test
    void actualizarUsuario_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> usuarioService.actualizarUsuario(99L, crearRequest("x", "x@x.cl")))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void cambiarEstadoUsuario_debeActualizarElEstado() {
        // GIVEN.
        UsuarioModel existente = crearUsuarioModel(1L, "jperez", "jperez@test.cl", true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.save(any(UsuarioModel.class))).thenReturn(existente);
        // WHEN.
        UsuarioResponse resultado = usuarioService.cambiarEstadoUsuario(1L, false);
        // THEN.
        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void eliminarUsuario_debeEliminarCuandoExiste() {
        // GIVEN.
        UsuarioModel existente = crearUsuarioModel(1L, "jperez", "jperez@test.cl", true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(existente));
        // WHEN.
        usuarioService.eliminarUsuario(1L);
        // THEN.
        verify(usuarioRepository).delete(existente);
    }

    @Test
    void eliminarUsuario_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> usuarioService.eliminarUsuario(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
