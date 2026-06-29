package cl.duoc.sucursales.service;

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

import cl.duoc.sucursales.dto.request.SucursalRequest;
import cl.duoc.sucursales.dto.response.SucursalResponse;
import cl.duoc.sucursales.exception.SucursalNoEncontradaException;
import cl.duoc.sucursales.model.SucursalModel;
import cl.duoc.sucursales.repository.SucursalRepository;

@ExtendWith(MockitoExtension.class)
class SucursalServiceTest {

    @Mock
    private SucursalRepository sucursalRepository;

    @InjectMocks
    private SucursalService sucursalService;

    private SucursalModel crearSucursalModel(Long id, String nombre, boolean activo) {
        return SucursalModel.builder()
                .idSucursal(id)
                .nombre(nombre)
                .direccion("Av. Principal 100")
                .comuna("Santiago")
                .telefono("+56912345678")
                .horarioApertura("09:00")
                .horarioCierre("20:00")
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private SucursalRequest crearRequest(String nombre, String apertura, String cierre) {
        SucursalRequest request = new SucursalRequest();
        request.setNombre(nombre);
        request.setDireccion("Av. Principal 100");
        request.setComuna("Santiago");
        request.setTelefono("+56912345678");
        request.setHorarioApertura(apertura);
        request.setHorarioCierre(cierre);
        request.setActivo(true);
        return request;
    }

    @Test
    void listarSucursales_debeRetornarTodas() {
        // GIVEN.
        when(sucursalRepository.findAll()).thenReturn(List.of(crearSucursalModel(1L, "Centro", true)));
        // WHEN.
        List<SucursalResponse> resultado = sucursalService.listarSucursales();
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void listarSucursalesActivas_debeFiltrarPorActivo() {
        // GIVEN.
        when(sucursalRepository.findByActivoTrue()).thenReturn(List.of(crearSucursalModel(1L, "Centro", true)));
        // WHEN.
        List<SucursalResponse> resultado = sucursalService.listarSucursalesActivas();
        // THEN.
        assertThat(resultado).allMatch(SucursalResponse::getActivo);
    }

    @Test
    void listarPorComuna_debeRetornarSucursalesDeLaComuna() {
        // GIVEN.
        when(sucursalRepository.findByComunaIgnoreCase("Santiago"))
                .thenReturn(List.of(crearSucursalModel(1L, "Centro", true)));
        // WHEN.
        List<SucursalResponse> resultado = sucursalService.listarPorComuna("Santiago");
        // THEN.
        assertThat(resultado).hasSize(1);
    }

    @Test
    void buscarPorId_debeRetornarSucursalCuandoExiste() {
        // GIVEN.
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(crearSucursalModel(1L, "Centro", true)));
        // WHEN.
        SucursalResponse resultado = sucursalService.buscarPorId(1L);
        // THEN.
        assertThat(resultado.getNombre()).isEqualTo("Centro");
    }

    @Test
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> sucursalService.buscarPorId(99L))
                .isInstanceOf(SucursalNoEncontradaException.class);
    }

    @Test
    void crearSucursal_debeCrearCuandoHorarioYNombreValidos() {
        // GIVEN.
        SucursalRequest request = crearRequest("Centro", "09:00", "20:00");
        when(sucursalRepository.findByNombreIgnoreCase("Centro")).thenReturn(Optional.empty());
        when(sucursalRepository.save(any(SucursalModel.class))).thenReturn(crearSucursalModel(1L, "Centro", true));
        // WHEN.
        SucursalResponse resultado = sucursalService.crearSucursal(request);
        // THEN.
        assertThat(resultado.getIdSucursal()).isEqualTo(1L);
    }

    @Test // Regla de negocio: el horario de cierre debe ser posterior al de apertura.
    void crearSucursal_debeLanzarExcepcionCuandoHorarioCierreNoEsPosteriorAApertura() {
        // GIVEN.
        SucursalRequest request = crearRequest("Centro", "20:00", "09:00");
        // WHEN + THEN.
        assertThatThrownBy(() -> sucursalService.crearSucursal(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("horario");
        verify(sucursalRepository, never()).save(any(SucursalModel.class));
    }

    @Test
    void crearSucursal_debeLanzarExcepcionCuandoNombreDuplicado() {
        // GIVEN.
        SucursalRequest request = crearRequest("Centro", "09:00", "20:00");
        when(sucursalRepository.findByNombreIgnoreCase("Centro"))
                .thenReturn(Optional.of(crearSucursalModel(5L, "Centro", true)));
        // WHEN + THEN.
        assertThatThrownBy(() -> sucursalService.crearSucursal(request))
                .isInstanceOf(IllegalArgumentException.class);
        verify(sucursalRepository, never()).save(any(SucursalModel.class));
    }

    @Test
    void actualizarSucursal_debeActualizarCuandoExiste() {
        // GIVEN.
        SucursalModel existente = crearSucursalModel(1L, "Centro", true);
        SucursalRequest request = crearRequest("Centro", "08:00", "22:00");
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(sucursalRepository.findByNombreIgnoreCase("Centro")).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(any(SucursalModel.class))).thenReturn(existente);
        // WHEN.
        SucursalResponse resultado = sucursalService.actualizarSucursal(1L, request);
        // THEN.
        assertThat(resultado.getHorarioApertura()).isEqualTo("08:00");
    }

    @Test
    void actualizarSucursal_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> sucursalService.actualizarSucursal(99L, crearRequest("X", "09:00", "20:00")))
                .isInstanceOf(SucursalNoEncontradaException.class);
    }

    @Test
    void cambiarEstadoSucursal_debeActualizarElEstado() {
        // GIVEN.
        SucursalModel existente = crearSucursalModel(1L, "Centro", true);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(sucursalRepository.save(any(SucursalModel.class))).thenReturn(existente);
        // WHEN.
        SucursalResponse resultado = sucursalService.cambiarEstadoSucursal(1L, false);
        // THEN.
        assertThat(resultado.getActivo()).isFalse();
    }

    @Test
    void eliminarSucursal_debeEliminarCuandoExiste() {
        // GIVEN.
        SucursalModel existente = crearSucursalModel(1L, "Centro", true);
        when(sucursalRepository.findById(1L)).thenReturn(Optional.of(existente));
        // WHEN.
        sucursalService.eliminarSucursal(1L);
        // THEN.
        verify(sucursalRepository).delete(existente);
    }

    @Test
    void eliminarSucursal_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(sucursalRepository.findById(99L)).thenReturn(Optional.empty());
        // WHEN + THEN.
        assertThatThrownBy(() -> sucursalService.eliminarSucursal(99L))
                .isInstanceOf(SucursalNoEncontradaException.class);
    }
}
