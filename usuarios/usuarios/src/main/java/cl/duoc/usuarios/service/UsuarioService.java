package cl.duoc.usuarios.service;

import cl.duoc.usuarios.dto.request.UsuarioRequest;
import cl.duoc.usuarios.dto.response.UsuarioResponse;
import cl.duoc.usuarios.exception.UsuarioNoEncontradoException;
import cl.duoc.usuarios.model.UsuarioModel;
import cl.duoc.usuarios.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> listarUsuarios() {
        log.info("Listando todos los usuarios");

        return usuarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<UsuarioResponse> listarUsuariosActivos() {
        log.info("Listando usuarios activos");

        return usuarioRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<UsuarioResponse> listarPorRol(String rol) {
        log.info("Listando usuarios por rol: {}", rol);

        return usuarioRepository.findByRolIgnoreCase(rol)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public UsuarioResponse buscarPorId(Long idUsuario) {
        log.info("Buscando usuario con ID: {}", idUsuario);

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        return mapearAResponse(usuario);
    }

    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        log.info("Creando usuario con username: {}", request.getUsername());

        usuarioRepository.findByUsernameIgnoreCase(request.getUsername())
                .ifPresent(usuario -> {
                    throw new IllegalArgumentException("Ya existe un usuario con el username: " + request.getUsername());
                });

        usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(usuario -> {
                    throw new IllegalArgumentException("Ya existe un usuario con el email: " + request.getEmail());
                });

        UsuarioModel usuario = UsuarioModel.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .rol(request.getRol())
                .idEmpleado(request.getIdEmpleado())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        UsuarioModel usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario creado correctamente con ID: {}", usuarioGuardado.getIdUsuario());

        return mapearAResponse(usuarioGuardado);
    }

    public UsuarioResponse actualizarUsuario(Long idUsuario, UsuarioRequest request) {
        log.info("Actualizando usuario con ID: {}", idUsuario);

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        usuarioRepository.findByUsernameIgnoreCase(request.getUsername())
                .ifPresent(usuarioExistente -> {
                    if (!usuarioExistente.getIdUsuario().equals(idUsuario)) {
                        throw new IllegalArgumentException("Ya existe otro usuario con el username: " + request.getUsername());
                    }
                });

        usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(usuarioExistente -> {
                    if (!usuarioExistente.getIdUsuario().equals(idUsuario)) {
                        throw new IllegalArgumentException("Ya existe otro usuario con el email: " + request.getEmail());
                    }
                });

        usuario.setUsername(request.getUsername());
        usuario.setEmail(request.getEmail());
        usuario.setPassword(request.getPassword());
        usuario.setRol(request.getRol());
        usuario.setIdEmpleado(request.getIdEmpleado());

        if (request.getActivo() != null) {
            usuario.setActivo(request.getActivo());
        }

        UsuarioModel usuarioActualizado = usuarioRepository.save(usuario);

        log.info("Usuario actualizado correctamente con ID: {}", usuarioActualizado.getIdUsuario());

        return mapearAResponse(usuarioActualizado);
    }

    public UsuarioResponse cambiarEstadoUsuario(Long idUsuario, Boolean activo) {
        log.info("Cambiando estado de usuario ID: {} a activo: {}", idUsuario, activo);

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        usuario.setActivo(activo);

        UsuarioModel usuarioActualizado = usuarioRepository.save(usuario);

        return mapearAResponse(usuarioActualizado);
    }

    public void eliminarUsuario(Long idUsuario) {
        log.info("Eliminando usuario con ID: {}", idUsuario);

        UsuarioModel usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario));

        usuarioRepository.delete(usuario);

        log.info("Usuario eliminado correctamente con ID: {}", idUsuario);
    }

    private UsuarioResponse mapearAResponse(UsuarioModel usuario) {
        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .username(usuario.getUsername())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .idEmpleado(usuario.getIdEmpleado())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .build();
    }
}