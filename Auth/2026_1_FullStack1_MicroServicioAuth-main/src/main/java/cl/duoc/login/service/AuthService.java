package cl.duoc.login.service;

import org.springframework.stereotype.Service;
import cl.duoc.login.dto.request.DtoAuthRequest;
import cl.duoc.login.dto.response.DtoAuthResponse;
import cl.duoc.login.model.UsuarioModel;
import cl.duoc.login.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public DtoAuthResponse login(DtoAuthRequest request) {
        UsuarioModel usuario = usuarioRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario o password incorrecto"));

        if (!usuario.getEnabled()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario deshabilitado");
        }

        if (!usuario.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o password incorrecto");
        }

        String token = jwtService.generarToken(
                usuario.getUsername(),
                usuario.getRole());

        return new DtoAuthResponse(token);
    }
}