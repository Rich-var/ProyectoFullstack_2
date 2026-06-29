package cl.duoc.pedidos.client;

import cl.duoc.pedidos.client.dto.ProductoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "productos-service")
public interface ProductoClient {
    @GetMapping("/api/productos/{idProducto}")
    ProductoDTO obtenerProductoPorId(@PathVariable("idProducto") Long idProducto);
}