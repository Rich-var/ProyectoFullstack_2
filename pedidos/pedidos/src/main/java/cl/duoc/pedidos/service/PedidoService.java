package cl.duoc.pedidos.service;

import cl.duoc.pedidos.client.ProductoClient;
import cl.duoc.pedidos.client.UsuarioClient;
import cl.duoc.pedidos.client.dto.ProductoDTO;
import cl.duoc.pedidos.client.dto.UsuarioDTO;
import cl.duoc.pedidos.dto.request.PedidoRequest;
import cl.duoc.pedidos.dto.response.PedidoResponse;
import cl.duoc.pedidos.exception.PedidoNoEncontradoException;
import cl.duoc.pedidos.model.PedidoModel;
import cl.duoc.pedidos.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoClient productoClient;
    private final UsuarioClient usuarioClient;

    public PedidoResponse crearPedido(PedidoRequest request) {
        log.info("Iniciando creación de pedido para el usuario ID: {}", request.getIdUsuario());

        // 1. Llamada a Usuarios-Service usando Feign
        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(request.getIdUsuario());
        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("El usuario está inactivo y no puede realizar pedidos");
        }

        // 2. Llamada a Productos-Service usando Feign
        ProductoDTO producto = productoClient.obtenerProductoPorId(request.getIdProducto());
        if (!producto.getActivo()) {
            throw new IllegalArgumentException("El producto seleccionado no está disponible");
        }

        // 3. Calcular el total del pedido
        Integer totalCalculado = producto.getPrecio() * request.getCantidad();

        // 4. Guardar en BD local de Pedidos
        PedidoModel pedido = PedidoModel.builder()
                .idUsuario(request.getIdUsuario())
                .idProducto(request.getIdProducto())
                .cantidad(request.getCantidad())
                .total(totalCalculado)
                .estado("PENDIENTE")
                .build();

        PedidoModel pedidoGuardado = pedidoRepository.save(pedido);
        
        return mapearAResponse(pedidoGuardado, usuario, producto);
    }

    public List<PedidoResponse> listarPedidos() {
        return pedidoRepository.findAll().stream()
                .map(pedido -> {
                    // Para simplificar, en listar traemos los datos remotos (Cuidado con el N+1 en prod real)
                    UsuarioDTO u = usuarioClient.obtenerUsuarioPorId(pedido.getIdUsuario());
                    ProductoDTO p = productoClient.obtenerProductoPorId(pedido.getIdProducto());
                    return mapearAResponse(pedido, u, p);
                }).toList();
    }

    public PedidoResponse obtenerPedidoPorId(Long idPedido) {
        log.info("Buscando pedido con ID: {}", idPedido);

        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.warn("No se encontró el pedido con ID: {}", idPedido);
                    return new PedidoNoEncontradoException("No se encontró el pedido con ID: " + idPedido);
                });

        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(pedido.getIdUsuario());
        ProductoDTO producto = productoClient.obtenerProductoPorId(pedido.getIdProducto());
        return mapearAResponse(pedido, usuario, producto);
    }

    public List<PedidoResponse> listarPedidosPorUsuario(Long idUsuario) {
        log.info("Listando pedidos del usuario ID: {}", idUsuario);

        return pedidoRepository.findByIdUsuario(idUsuario).stream()
                .map(pedido -> {
                    UsuarioDTO u = usuarioClient.obtenerUsuarioPorId(pedido.getIdUsuario());
                    ProductoDTO p = productoClient.obtenerProductoPorId(pedido.getIdProducto());
                    return mapearAResponse(pedido, u, p);
                }).toList();
    }

    public PedidoResponse actualizarEstadoPedido(Long idPedido, String nuevoEstado) {
        log.info("Actualizando estado del pedido ID: {} a '{}'", idPedido, nuevoEstado);

        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.warn("No se encontró el pedido con ID: {} al intentar actualizar su estado", idPedido);
                    return new PedidoNoEncontradoException("No se encontró el pedido con ID: " + idPedido);
                });

        pedido.setEstado(nuevoEstado.toUpperCase());
        PedidoModel pedidoActualizado = pedidoRepository.save(pedido);

        UsuarioDTO usuario = usuarioClient.obtenerUsuarioPorId(pedidoActualizado.getIdUsuario());
        ProductoDTO producto = productoClient.obtenerProductoPorId(pedidoActualizado.getIdProducto());
        return mapearAResponse(pedidoActualizado, usuario, producto);
    }

    public void eliminarPedido(Long idPedido) {
        log.info("Eliminando pedido con ID: {}", idPedido);

        PedidoModel pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> {
                    log.warn("No se encontró el pedido con ID: {} al intentar eliminarlo", idPedido);
                    return new PedidoNoEncontradoException("No se encontró el pedido con ID: " + idPedido);
                });

        pedidoRepository.delete(pedido);
    }

    private PedidoResponse mapearAResponse(PedidoModel pedido, UsuarioDTO usuario, ProductoDTO producto) {
        return PedidoResponse.builder()
                .idPedido(pedido.getIdPedido())
                .usuario(usuario)
                .producto(producto)
                .cantidad(pedido.getCantidad())
                .total(pedido.getTotal())
                .estado(pedido.getEstado())
                .fechaCreacion(pedido.getFechaCreacion())
                .build();
    }
}