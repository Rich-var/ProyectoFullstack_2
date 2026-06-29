package cl.duoc.empleados.service;

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

import cl.duoc.empleados.dto.request.EmpleadoRequest;
import cl.duoc.empleados.dto.response.EmpleadoResponse;
import cl.duoc.empleados.exception.EmpleadoNoEncontradoException;
import cl.duoc.empleados.model.EmpleadoModel;
import cl.duoc.empleados.repository.EmpleadoRepository;

@ExtendWith(MockitoExtension.class) // Pruebas unitarias puras: no se levanta el contexto de Spring.
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository; // Dependencia simulada del service.

    @InjectMocks
    private EmpleadoService empleadoService; // Clase real bajo prueba.

    private EmpleadoModel crearEmpleadoModel(Long id, String rut, String email, boolean activo) {
        return EmpleadoModel.builder()
                .idEmpleado(id)
                .rut(rut)
                .nombres("Juan")
                .apellidos("Perez")
                .email(email)
                .telefono("+56912345678")
                .cargo("Barista")
                .idSucursal(1L)
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private EmpleadoRequest crearRequestValido(String rut, String email) {
        EmpleadoRequest request = new EmpleadoRequest();
        request.setRut(rut);
        request.setNombres("Juan");
        request.setApellidos("Perez");
        request.setEmail(email);
        request.setTelefono("+56912345678");
        request.setCargo("Barista");
        request.setIdSucursal(1L);
        request.setActivo(true);
        return request;
    }

    @Test
    void listarEmpleados_debeRetornarTodos() {
        // GIVEN.
        when(empleadoRepository.findAll())
                .thenReturn(List.of(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true)));
        // WHEN.
        List<EmpleadoResponse> resultado = empleadoService.listarEmpleados();
        // THEN.
        assertThat(resultado).hasSize(1);
        verify(empleadoRepository).findAll();
    }

    @Test
    void listarEmpleadosActivos_debeFiltrarPorActivo() {
        // GIVEN.
        when(empleadoRepository.findByActivoTrue())
                .thenReturn(List.of(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true)));
        // WHEN.
        List<EmpleadoResponse> resultado = empleadoService.listarEmpleadosActivos();
        // THEN.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
    }

    @Test
    void listarPorCargo_debeRetornarEmpleadosDelCargo() {
        // GIVEN.
        when(empleadoRepository.findByCargoIgnoreCase("Barista"))
                .thenReturn(List.of(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true)));
        // WHEN.
        List<EmpleadoResponse> resultado = empleadoService.listarPorCargo("Barista");
        // THEN.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCargo()).isEqualTo("Barista");
    }

    @Test
    void listarPorSucursal_debeRetornarEmpleadosDeLaSucursal() {
        // GIVEN.
        when(empleadoRepository.findByIdSucursal(1L))
                .thenReturn(List.of(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true)));
        // WHEN.
        List<EmpleadoResponse> resultado = empleadoService.listarPorSucursal(1L);
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_debeRetornarEmpleadoCuandoExiste() {
        // GIVEN.
        when(empleadoRepository.findById(1L))
                .thenReturn(Optional.of(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true)));
        // WHEN.
        EmpleadoResponse resultado = empleadoService.buscarPorId(1L);
        // THEN.
        assertThat(resultado.getIdEmpleado()).isEqualTo(1L);
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> empleadoService.buscarPorId(99L))
                .isInstanceOf(EmpleadoNoEncontradoException.class);
    }

    @Test
    void crearEmpleado_debeCrearCuandoRutYEmailNoExisten() {
        // GIVEN.
        EmpleadoRequest request = crearRequestValido("11111111-1", "juan@test.cl");
        when(empleadoRepository.findByRutIgnoreCase("11111111-1")).thenReturn(Optional.empty());
        when(empleadoRepository.findByEmailIgnoreCase("juan@test.cl")).thenReturn(Optional.empty());
        when(empleadoRepository.save(any(EmpleadoModel.class)))
                .thenReturn(crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true));
        // WHEN.
        EmpleadoResponse resultado = empleadoService.crearEmpleado(request);
        // THEN.
        assertThat(resultado.getIdEmpleado()).isEqualTo(1L);
        verify(empleadoRepository).save(any(EmpleadoModel.class));
    }

    @Test
    void crearEmpleado_debeLanzarExcepcionCuandoRutDuplicado() {
        // GIVEN.
        EmpleadoRequest request = crearRequestValido("11111111-1", "nuevo@test.cl");
        when(empleadoRepository.findByRutIgnoreCase("11111111-1"))
                .thenReturn(Optional.of(crearEmpleadoModel(5L, "11111111-1", "otro@test.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> empleadoService.crearEmpleado(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("RUT");
        verify(empleadoRepository, never()).save(any(EmpleadoModel.class));
    }

    @Test
    void crearEmpleado_debeLanzarExcepcionCuandoEmailDuplicado() {
        // GIVEN.
        EmpleadoRequest request = crearRequestValido("22222222-2", "juan@test.cl");
        when(empleadoRepository.findByRutIgnoreCase("22222222-2")).thenReturn(Optional.empty());
        when(empleadoRepository.findByEmailIgnoreCase("juan@test.cl"))
                .thenReturn(Optional.of(crearEmpleadoModel(5L, "33333333-3", "juan@test.cl", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> empleadoService.crearEmpleado(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("email");
        verify(empleadoRepository, never()).save(any(EmpleadoModel.class));
    }

    @Test
    void actualizarEmpleado_debeActualizarCuandoExiste() {
        // GIVEN.
        EmpleadoModel existente = crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true);
        EmpleadoRequest request = crearRequestValido("11111111-1", "juan@test.cl");
        request.setCargo("Supervisor");
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empleadoRepository.findByRutIgnoreCase("11111111-1")).thenReturn(Optional.of(existente));
        when(empleadoRepository.findByEmailIgnoreCase("juan@test.cl")).thenReturn(Optional.of(existente));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(existente);
        // WHEN.
        EmpleadoResponse resultado = empleadoService.actualizarEmpleado(1L, request);
        // THEN.
        assertThat(resultado.getCargo()).isEqualTo("Supervisor");
        verify(empleadoRepository).save(existente);
    }

    @Test
    void actualizarEmpleado_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> empleadoService.actualizarEmpleado(99L, crearRequestValido("1-1", "a@a.cl")))
                .isInstanceOf(EmpleadoNoEncontradoException.class);
        verify(empleadoRepository, never()).save(any(EmpleadoModel.class));
    }

    @Test
    void cambiarEstadoEmpleado_debeActualizarElEstado() {
        // GIVEN.
        EmpleadoModel existente = crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(empleadoRepository.save(any(EmpleadoModel.class))).thenReturn(existente);
        // WHEN.
        EmpleadoResponse resultado = empleadoService.cambiarEstadoEmpleado(1L, false);
        // THEN.
        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void eliminarEmpleado_debeEliminarCuandoExiste() {
        // GIVEN.
        EmpleadoModel existente = crearEmpleadoModel(1L, "11111111-1", "juan@test.cl", true);
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(existente));
        // WHEN.
        empleadoService.eliminarEmpleado(1L);
        // THEN.
        verify(empleadoRepository).delete(existente);
    }

    @Test
    void eliminarEmpleado_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> empleadoService.eliminarEmpleado(99L))
                .isInstanceOf(EmpleadoNoEncontradoException.class);
        verify(empleadoRepository, never()).delete(any(EmpleadoModel.class));
    }
}
