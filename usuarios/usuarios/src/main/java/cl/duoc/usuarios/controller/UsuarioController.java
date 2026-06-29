package cl.duoc.usuarios.controller;

import cl.duoc.usuarios.dto.request.UsuarioRequest;
import cl.duoc.usuarios.dto.response.UsuarioResponse;
import cl.duoc.usuarios.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<UsuarioResponse>> listarUsuariosActivos() {
        return ResponseEntity.ok(usuarioService.listarUsuariosActivos());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioResponse>> listarPorRol(@PathVariable String rol) {
        return ResponseEntity.ok(usuarioService.listarPorRol(rol));
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(usuarioService.buscarPorId(idUsuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long idUsuario,
            @Valid @RequestBody UsuarioRequest request
    ) {
        return ResponseEntity.ok(usuarioService.actualizarUsuario(idUsuario, request));
    }

    @PatchMapping("/{idUsuario}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstadoUsuario(
            @PathVariable Long idUsuario,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(usuarioService.cambiarEstadoUsuario(idUsuario, activo));
    }

    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}