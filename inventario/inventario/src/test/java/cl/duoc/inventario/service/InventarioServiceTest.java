package cl.duoc.inventario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.inventario.dto.request.InventarioRequest;
import cl.duoc.inventario.dto.response.InventarioResponse;
import cl.duoc.inventario.exception.InventarioNoEncontradoException;
import cl.duoc.inventario.model.InventarioModel;
import cl.duoc.inventario.repository.InventarioRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private InventarioModel crearInventarioModel(Long id, Long idProducto, Long idSucursal, int cantidad) {
        return InventarioModel.builder()
                .idInventario(id)
                .idProducto(idProducto)
                .idSucursal(idSucursal)
                .cantidad(cantidad)
                .fechaActualizacion(LocalDateTime.now())
                .build();
    }

    private InventarioRequest crearRequest(Long idProducto, Long idSucursal, int cantidad) {
        InventarioRequest request = new InventarioRequest();
        request.setIdProducto(idProducto);
        request.setIdSucursal(idSucursal);
        request.setCantidad(cantidad);
        return request;
    }

    @Test // Caso: consulta de stock cuando el registro existe.
    void obtenerStock_debeRetornarStockCuandoExiste() {
        // GIVEN.
        when(inventarioRepository.findByIdProductoAndIdSucursal(10L, 1L))
                .thenReturn(Optional.of(crearInventarioModel(1L, 10L, 1L, 50)));
        // WHEN.
        InventarioResponse resultado = inventarioService.obtenerStock(10L, 1L);
        // THEN.
        assertThat(resultado.getCantidad()).isEqualTo(50);
    }

    @Test // Caso: consulta de stock cuando no existe registro para ese producto/sucursal.
    void obtenerStock_debeLanzarExcepcionCuandoNoExisteRegistro() {
        // GIVEN.
        when(inventarioRepository.findByIdProductoAndIdSucursal(10L, 1L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> inventarioService.obtenerStock(10L, 1L))
                .isInstanceOf(InventarioNoEncontradoException.class);
    }

    @Test // Caso: actualizar stock cuando ya existe un registro previo.
    void actualizarStock_debeActualizarCantidadCuandoYaExisteRegistro() {
        // GIVEN.
        InventarioModel existente = crearInventarioModel(1L, 10L, 1L, 20);
        when(inventarioRepository.findByIdProductoAndIdSucursal(10L, 1L)).thenReturn(Optional.of(existente));
        when(inventarioRepository.save(any(InventarioModel.class))).thenReturn(existente);
        // WHEN.
        InventarioResponse resultado = inventarioService.actualizarStock(crearRequest(10L, 1L, 75));
        // THEN.
        assertThat(resultado).isNotNull();
        verify(inventarioRepository).save(existente);
        assertThat(existente.getCantidad()).isEqualTo(75);
    }

    @Test // Caso: actualizar stock cuando NO existe registro previo, debe crear uno nuevo.
    void actualizarStock_debeCrearRegistroCuandoNoExistePrevio() {
        // GIVEN.
        when(inventarioRepository.findByIdProductoAndIdSucursal(20L, 2L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(InventarioModel.class)))
                .thenReturn(crearInventarioModel(2L, 20L, 2L, 30));
        // WHEN.
        InventarioResponse resultado = inventarioService.actualizarStock(crearRequest(20L, 2L, 30));
        // THEN.
        assertThat(resultado.getIdProducto()).isEqualTo(20L);
        assertThat(resultado.getCantidad()).isEqualTo(30);
        verify(inventarioRepository).save(any(InventarioModel.class));
    }
}
