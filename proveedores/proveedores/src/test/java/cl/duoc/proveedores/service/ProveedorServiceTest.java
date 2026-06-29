package cl.duoc.proveedores.service;

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

import cl.duoc.proveedores.dto.request.ProveedorRequest;
import cl.duoc.proveedores.dto.response.ProveedorResponse;
import cl.duoc.proveedores.exception.ProveedorNoEncontradoException;
import cl.duoc.proveedores.model.ProveedorModel;
import cl.duoc.proveedores.repository.ProveedorRepository;

@ExtendWith(MockitoExtension.class)
class ProveedorServiceTest {

    @Mock
    private ProveedorRepository proveedorRepository;

    @InjectMocks
    private ProveedorService proveedorService;

    private ProveedorModel crearProveedorModel(Long id, String nombre, String email, boolean activo) {
        return ProveedorModel.builder()
                .idProveedor(id)
                .nombre(nombre)
                .rubro("Insumos")
                .telefono("+56912345678")
                .email(email)
                .direccion("Av. Siempre Viva 123")
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private ProveedorRequest crearRequest(String nombre, String email) {
        ProveedorRequest request = new ProveedorRequest();
        request.setNombre(nombre);
        request.setRubro("Insumos");
        request.setTelefono("+56912345678");
        request.setEmail(email);
        request.setDireccion("Av. Siempre Viva 123");
        request.setActivo(true);
        return request;
    }

    @Test
    void listarProveedores_debeRetornarTodos() {
        // GIVEN.
        when(proveedorRepository.findAll())
                .thenReturn(List.of(crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true)));
        // WHEN.
        List<ProveedorResponse> resultado = proveedorService.listarProveedores();
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarProveedoresActivos_debeFiltrarPorActivo() {
        // GIVEN.
        when(proveedorRepository.findByActivoTrue())
                .thenReturn(List.of(crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true)));
        // WHEN.
        List<ProveedorResponse> resultado = proveedorService.listarProveedoresActivos();
        // THEN.
        assertThat(resultado).allMatch(ProveedorResponse::getActivo);
    }

    @Test
    void listarPorRubro_debeRetornarProveedoresDelRubro() {
        // GIVEN.
        when(proveedorRepository.findByRubroIgnoreCase("Insumos"))
                .thenReturn(List.of(crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true)));
        // WHEN.
        List<ProveedorResponse> resultado = proveedorService.listarPorRubro("Insumos");
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_debeRetornarProveedorCuandoExiste() {
        // GIVEN.
        when(proveedorRepository.findById(1L))
                .thenReturn(Optional.of(crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true)));
        // WHEN.
        ProveedorResponse resultado = proveedorService.buscarPorId(1L);
        // THEN.
        assertThat(resultado.getNombre()).isEqualTo("Cafe Sur");
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> proveedorService.buscarPorId(99L))
                .isInstanceOf(ProveedorNoEncontradoException.class);
    }

    @Test
    void crearProveedor_debeCrearCuandoNombreYEmailNoExisten() {
        // GIVEN.
        ProveedorRequest request = crearRequest("Cafe Sur", "ventas@cafesur.cl");
        when(proveedorRepository.findByNombreIgnoreCase("Cafe Sur")).thenReturn(Optional.empty());
        when(proveedorRepository.findByEmailIgnoreCase("ventas@cafesur.cl")).thenReturn(Optional.empty());
        when(proveedorRepository.save(any(ProveedorModel.class)))
                .thenReturn(crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true));
        // WHEN.
        ProveedorResponse resultado = proveedorService.crearProveedor(request);
        // THEN.
        assertThat(resultado.getIdProveedor()).isEqualTo(1L);
    }

    @Test
    void crearProveedor_debeLanzarExcepcionCuandoNombreDuplicado() {
        // GIVEN.
        ProveedorRequest request = crearRequest("Cafe Sur", "nuevo@cafesur.cl");
        when(proveedorRepository.findByNombreIgnoreCase("Cafe Sur"))
                .thenReturn(Optional.of(crearProveedorModel(5L, "Cafe Sur", "otro@cafesur.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> proveedorService.crearProveedor(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(proveedorRepository, never()).save(any(ProveedorModel.class));
    }

    @Test
    void crearProveedor_debeLanzarExcepcionCuandoEmailDuplicado() {
        // GIVEN.
        ProveedorRequest request = crearRequest("Cafe Norte", "ventas@cafesur.cl");
        when(proveedorRepository.findByNombreIgnoreCase("Cafe Norte")).thenReturn(Optional.empty());
        when(proveedorRepository.findByEmailIgnoreCase("ventas@cafesur.cl"))
                .thenReturn(Optional.of(crearProveedorModel(5L, "Cafe Sur", "ventas@cafesur.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> proveedorService.crearProveedor(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(proveedorRepository, never()).save(any(ProveedorModel.class));
    }

    @Test
    void actualizarProveedor_debeActualizarCuandoExiste() {
        // GIVEN.
        ProveedorModel existente = crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true);
        ProveedorRequest request = crearRequest("Cafe Sur", "ventas@cafesur.cl");
        request.setRubro("Distribucion");
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.findByNombreIgnoreCase("Cafe Sur")).thenReturn(Optional.of(existente));
        when(proveedorRepository.findByEmailIgnoreCase("ventas@cafesur.cl")).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any(ProveedorModel.class))).thenReturn(existente);
        // WHEN.
        ProveedorResponse resultado = proveedorService.actualizarProveedor(1L, request);
        // THEN.
        assertThat(resultado.getRubro()).isEqualTo("Distribucion");
    }

    @Test
    void actualizarProveedor_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> proveedorService.actualizarProveedor(99L, crearRequest("X", "x@x.cl")))
                .isInstanceOf(ProveedorNoEncontradoException.class);
    }

    @Test
    void cambiarEstadoProveedor_debeActualizarElEstado() {
        // GIVEN.
        ProveedorModel existente = crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true);
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(proveedorRepository.save(any(ProveedorModel.class))).thenReturn(existente);
        // WHEN.
        ProveedorResponse resultado = proveedorService.cambiarEstadoProveedor(1L, false);
        // THEN.
        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void eliminarProveedor_debeEliminarCuandoExiste() {
        // GIVEN.
        ProveedorModel existente = crearProveedorModel(1L, "Cafe Sur", "ventas@cafesur.cl", true);
        when(proveedorRepository.findById(1L)).thenReturn(Optional.of(existente));
        // WHEN.
        proveedorService.eliminarProveedor(1L);
        // THEN.
        verify(proveedorRepository).delete(existente);
    }

    @Test
    void eliminarProveedor_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(proveedorRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> proveedorService.eliminarProveedor(99L))
                .isInstanceOf(ProveedorNoEncontradoException.class);
    }
}
