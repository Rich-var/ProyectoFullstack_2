package cl.duoc.pedidos.client;

import cl.duoc.pedidos.client.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-service")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{idUsuario}")
    UsuarioDTO obtenerUsuarioPorId(@PathVariable("idUsuario") Long idUsuario);
}