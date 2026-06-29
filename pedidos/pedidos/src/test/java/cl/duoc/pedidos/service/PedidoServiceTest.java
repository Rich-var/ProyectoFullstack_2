package cl.duoc.pedidos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
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

import cl.duoc.pedidos.client.ProductoClient;
import cl.duoc.pedidos.client.UsuarioClient;
import cl.duoc.pedidos.client.dto.ProductoDTO;
import cl.duoc.pedidos.client.dto.UsuarioDTO;
import cl.duoc.pedidos.dto.request.PedidoRequest;
import cl.duoc.pedidos.dto.response.PedidoResponse;
import cl.duoc.pedidos.exception.PedidoNoEncontradoException;
import cl.duoc.pedidos.model.PedidoModel;
import cl.duoc.pedidos.repository.PedidoRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository; // Persistencia local de Pedidos.

    @Mock
    private ProductoClient productoClient; // Cliente Feign hacia productos-service (se simula, no se llama por red).

    @Mock
    private UsuarioClient usuarioClient; // Cliente Feign hacia usuarios-service (se simula, no se llama por red).

    @InjectMocks
    private PedidoService pedidoService;

    private UsuarioDTO crearUsuarioDto(Long id, boolean activo) {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setIdUsuario(id);
        usuario.setUsername("cliente_test");
        usuario.setEmail("cliente@test.cl");
        usuario.setActivo(activo);
        return usuario;
    }

    private ProductoDTO crearProductoDto(Long id, int precio, boolean activo) {
        ProductoDTO producto = new ProductoDTO();
        producto.setIdProducto(id);
        producto.setNombre("Cafe Latte");
        producto.setPrecio(precio);
        producto.setActivo(activo);
        return producto;
    }

    private PedidoRequest crearRequest(Long idUsuario, Long idProducto, int cantidad) {
        PedidoRequest request = new PedidoRequest();
        request.setIdUsuario(idUsuario);
        request.setIdProducto(idProducto);
        request.setCantidad(cantidad);
        return request;
    }

    @Test // Caso feliz: usuario activo + producto activo -> se calcula el total y se guarda el pedido.
    void crearPedido_debeCrearPedidoYCalcularTotalCuandoUsuarioYProductoSonValidos() {
        // GIVEN.
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, true));
        PedidoModel pedidoGuardado = PedidoModel.builder()
                .idPedido(100L)
                .idUsuario(1L)
                .idProducto(10L)
                .cantidad(2)
                .total(5000)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(pedidoGuardado);

        // WHEN.
        PedidoResponse resultado = pedidoService.crearPedido(crearRequest(1L, 10L, 2));

        // THEN: el total debe ser precio * cantidad (2500 * 2 = 5000).
        assertThat(resultado.getTotal()).isEqualTo(5000);
        assertThat(resultado.getEstado()).isEqualTo("PENDIENTE");
        verify(usuarioClient).obtenerUsuarioPorId(1L);
        verify(productoClient).obtenerProductoPorId(10L);
        verify(pedidoRepository).save(any(PedidoModel.class));
    }

    @Test // Caso de error: el usuario remoto esta inactivo, no debe crearse el pedido.
    void crearPedido_debeLanzarExcepcionCuandoUsuarioEstaInactivo() {
        // GIVEN.
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, false));

        // WHEN + THEN.
        assertThatThrownBy(() -> pedidoService.crearPedido(crearRequest(1L, 10L, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactivo");

        verify(productoClient, never()).obtenerProductoPorId(any());
        verify(pedidoRepository, never()).save(any(PedidoModel.class));
    }

    @Test // Caso de error: el producto remoto no esta disponible, no debe crearse el pedido.
    void crearPedido_debeLanzarExcepcionCuandoProductoNoEstaDisponible() {
        // GIVEN.
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, false));

        // WHEN + THEN.
        assertThatThrownBy(() -> pedidoService.crearPedido(crearRequest(1L, 10L, 2)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("disponible");

        verify(pedidoRepository, never()).save(any(PedidoModel.class));
    }

    @Test // Caso: listar pedidos enriquece cada registro local con datos remotos de usuario y producto.
    void listarPedidos_debeEnriquecerCadaPedidoConDatosRemotos() {
        // GIVEN.
        PedidoModel pedido = PedidoModel.builder()
                .idPedido(100L)
                .idUsuario(1L)
                .idProducto(10L)
                .cantidad(1)
                .total(2500)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, true));

        // WHEN.
        List<PedidoResponse> resultado = pedidoService.listarPedidos();

        // THEN.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getUsuario().getIdUsuario()).isEqualTo(1L);
        assertThat(resultado.get(0).getProducto().getIdProducto()).isEqualTo(10L);
    }

    @Test // Caso feliz: el pedido existe localmente y se enriquece con los datos remotos.
    void obtenerPedidoPorId_debeRetornarPedidoCuandoExiste() {
        // GIVEN.
        PedidoModel pedido = PedidoModel.builder()
                .idPedido(100L)
                .idUsuario(1L)
                .idProducto(10L)
                .cantidad(3)
                .total(7500)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, true));

        // WHEN.
        PedidoResponse resultado = pedidoService.obtenerPedidoPorId(100L);

        // THEN.
        assertThat(resultado.getIdPedido()).isEqualTo(100L);
        assertThat(resultado.getTotal()).isEqualTo(7500);
    }

    @Test // Caso de error: no existe el pedido -> debe lanzar PedidoNoEncontradoException y no llamar a los Feign Client.
    void obtenerPedidoPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> pedidoService.obtenerPedidoPorId(999L))
                .isInstanceOf(PedidoNoEncontradoException.class)
                .hasMessageContaining("999");

        verify(usuarioClient, never()).obtenerUsuarioPorId(any());
        verify(productoClient, never()).obtenerProductoPorId(any());
    }

    @Test // Caso: listar pedidos filtrando por usuario, delega correctamente al repositorio.
    void listarPedidosPorUsuario_debeRetornarSoloLosPedidosDeEseUsuario() {
        // GIVEN.
        PedidoModel pedido = PedidoModel.builder()
                .idPedido(100L)
                .idUsuario(1L)
                .idProducto(10L)
                .cantidad(1)
                .total(2500)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(pedidoRepository.findByIdUsuario(1L)).thenReturn(List.of(pedido));
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, true));

        // WHEN.
        List<PedidoResponse> resultado = pedidoService.listarPedidosPorUsuario(1L);

        // THEN.
        assertThat(resultado).hasSize(1);
        verify(pedidoRepository).findByIdUsuario(1L);
    }

    @Test // Caso feliz: actualizar el estado de un pedido existente (ej: PENDIENTE -> PREPARANDO).
    void actualizarEstadoPedido_debeActualizarEstadoCuandoElPedidoExiste() {
        // GIVEN.
        PedidoModel pedido = PedidoModel.builder()
                .idPedido(100L)
                .idUsuario(1L)
                .idProducto(10L)
                .cantidad(2)
                .total(5000)
                .estado("PENDIENTE")
                .fechaCreacion(LocalDateTime.now())
                .build();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoModel.class))).thenReturn(pedido);
        when(usuarioClient.obtenerUsuarioPorId(1L)).thenReturn(crearUsuarioDto(1L, true));
        when(productoClient.obtenerProductoPorId(10L)).thenReturn(crearProductoDto(10L, 2500, true));

        // WHEN.
        PedidoResponse resultado = pedidoService.actualizarEstadoPedido(100L, "preparando");

        // THEN: el service normaliza el estado a mayúsculas antes de guardar.
        assertThat(resultado.getEstado()).isEqualTo("PREPARANDO");
        verify(pedidoRepository).save(pedido);
    }

    @Test // Caso de error: actualizar estado de un pedido que no existe -> PedidoNoEncontradoException.
    void actualizarEstadoPedido_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> pedidoService.actualizarEstadoPedido(999L, "ENTREGADO"))
                .isInstanceOf(PedidoNoEncontradoException.class);

        verify(pedidoRepository, never()).save(any(PedidoModel.class));
    }

    @Test // Caso feliz: eliminar un pedido existente.
    void eliminarPedido_debeEliminarCuandoElPedidoExiste() {
        // GIVEN.
        PedidoModel pedido = PedidoModel.builder().idPedido(100L).build();
        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

        // WHEN.
        pedidoService.eliminarPedido(100L);

        // THEN.
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test // Caso de error: eliminar un pedido que no existe -> PedidoNoEncontradoException, sin tocar el repositorio.
    void eliminarPedido_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(pedidoRepository.findById(999L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> pedidoService.eliminarPedido(999L))
                .isInstanceOf(PedidoNoEncontradoException.class)
                .hasMessageContaining("999");

        verify(pedidoRepository, never()).delete(any(PedidoModel.class));
    }
}
