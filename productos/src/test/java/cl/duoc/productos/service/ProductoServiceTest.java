package cl.duoc.productos.service;

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

import cl.duoc.productos.dto.request.ProductoRequest;
import cl.duoc.productos.dto.response.ProductoResponse;
import cl.duoc.productos.exception.ProductoNoEncontradoException;
import cl.duoc.productos.model.ProductoModel;
import cl.duoc.productos.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoModel crearProductoModel(Long id, String nombre, int precio, boolean activo) {
        return ProductoModel.builder()
                .idProducto(id)
                .nombre(nombre)
                .descripcion("Descripcion de prueba")
                .precio(precio)
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private ProductoRequest crearRequest(String nombre, int precio) {
        ProductoRequest request = new ProductoRequest();
        request.setNombre(nombre);
        request.setDescripcion("Descripcion de prueba");
        request.setPrecio(precio);
        request.setActivo(true);
        return request;
    }

    @Test
    void listarProductos_debeRetornarTodos() {
        // GIVEN.
        when(productoRepository.findAll()).thenReturn(List.of(crearProductoModel(1L, "Cafe Latte", 2500, true)));
        // WHEN.
        List<ProductoResponse> resultado = productoService.listarProductos();
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarProductosActivos_debeFiltrarPorActivo() {
        // GIVEN.
        when(productoRepository.findByActivoTrue())
                .thenReturn(List.of(crearProductoModel(1L, "Cafe Latte", 2500, true)));
        // WHEN.
        List<ProductoResponse> resultado = productoService.listarProductosActivos();
        // THEN.
        assertThat(resultado).allMatch(ProductoResponse::getActivo);
    }

    @Test
    void buscarPorId_debeRetornarProductoCuandoExiste() {
        // GIVEN.
        when(productoRepository.findById(1L)).thenReturn(Optional.of(crearProductoModel(1L, "Cafe Latte", 2500, true)));
        // WHEN.
        ProductoResponse resultado = productoService.buscarPorId(1L);
        // THEN.
        assertThat(resultado.getNombre()).isEqualTo("Cafe Latte");
        assertThat(resultado.getPrecio()).isEqualTo(2500);
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> productoService.buscarPorId(99L))
                .isInstanceOf(ProductoNoEncontradoException.class);
    }

    @Test
    void crearProducto_debeCrearCuandoNombreNoExiste() {
        // GIVEN.
        ProductoRequest request = crearRequest("Cafe Latte", 2500);
        when(productoRepository.findByNombreIgnoreCase("Cafe Latte")).thenReturn(Optional.empty());
        when(productoRepository.save(any(ProductoModel.class)))
                .thenReturn(crearProductoModel(1L, "Cafe Latte", 2500, true));
        // WHEN.
        ProductoResponse resultado = productoService.crearProducto(request);
        // THEN.
        assertThat(resultado.getIdProducto()).isEqualTo(1L);
        verify(productoRepository).save(any(ProductoModel.class));
    }

    @Test
    void crearProducto_debeLanzarExcepcionCuandoNombreDuplicado() {
        // GIVEN.
        ProductoRequest request = crearRequest("Cafe Latte", 2500);
        when(productoRepository.findByNombreIgnoreCase("Cafe Latte"))
                .thenReturn(Optional.of(crearProductoModel(5L, "Cafe Latte", 2200, true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> productoService.crearProducto(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(productoRepository, never()).save(any(ProductoModel.class));
    }

    @Test
    void actualizarProducto_debeActualizarCuandoExiste() {
        // GIVEN.
        ProductoModel existente = crearProductoModel(1L, "Cafe Latte", 2500, true);
        ProductoRequest request = crearRequest("Cafe Latte", 2800);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.findByNombreIgnoreCase("Cafe Latte")).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(existente);
        // WHEN.
        ProductoResponse resultado = productoService.actualizarProducto(1L, request);
        // THEN.
        assertThat(resultado.getPrecio()).isEqualTo(2800);
    }

    @Test
    void actualizarProducto_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> productoService.actualizarProducto(99L, crearRequest("X", 100)))
                .isInstanceOf(ProductoNoEncontradoException.class);
        verify(productoRepository, never()).save(any(ProductoModel.class));
    }

    @Test
    void cambiarEstadoProducto_debeActualizarElEstado() {
        // GIVEN.
        ProductoModel existente = crearProductoModel(1L, "Cafe Latte", 2500, true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(productoRepository.save(any(ProductoModel.class))).thenReturn(existente);
        // WHEN.
        ProductoResponse resultado = productoService.cambiarEstadoProducto(1L, false);
        // THEN.
        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void eliminarProducto_debeEliminarCuandoExiste() {
        // GIVEN.
        ProductoModel existente = crearProductoModel(1L, "Cafe Latte", 2500, true);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(existente));
        // WHEN.
        productoService.eliminarProducto(1L);
        // THEN.
        verify(productoRepository).delete(existente);
    }

    @Test
    void eliminarProducto_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> productoService.eliminarProducto(99L))
                .isInstanceOf(ProductoNoEncontradoException.class);
        verify(productoRepository, never()).delete(any(ProductoModel.class));
    }
}
