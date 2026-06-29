package cl.duoc.categorias.service; // Mismo paquete que la clase real, requerido para pruebas unitarias limpias.

import static org.assertj.core.api.Assertions.assertThat; // Aserciones legibles estilo AssertJ.
import static org.assertj.core.api.Assertions.assertThatThrownBy; // Permite validar excepciones esperadas.
import static org.mockito.ArgumentMatchers.any; // Acepta cualquier instancia de un tipo dado.
import static org.mockito.Mockito.never; // Verifica que un metodo nunca fue invocado.
import static org.mockito.Mockito.verify; // Verifica interacciones con los mocks.
import static org.mockito.Mockito.when; // Configura el comportamiento de los mocks.

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.categorias.dto.request.CategoriaRequest;
import cl.duoc.categorias.dto.response.CategoriaResponse;
import cl.duoc.categorias.exception.CategoriaNoEncontradaException;
import cl.duoc.categorias.model.CategoriaModel;
import cl.duoc.categorias.repository.CategoriaRepository;

@ExtendWith(MockitoExtension.class) // Activa Mockito sin levantar el contexto de Spring (prueba unitaria pura).
class CategoriaServiceTest { // Clase de pruebas de la capa de negocio de Categorias.

    @Mock // Repositorio simulado: nunca toca una base de datos real.
    private CategoriaRepository categoriaRepository;

    @InjectMocks // Instancia real de CategoriaService con el mock inyectado por constructor.
    private CategoriaService categoriaService;

    private CategoriaModel crearCategoriaModel(Long id, String nombre, boolean activo) {
        return CategoriaModel.builder() // Construye una entidad de prueba reutilizable.
                .idCategoria(id)
                .nombre(nombre)
                .descripcion("Descripcion de prueba")
                .activo(activo)
                .fechaCreacion(LocalDateTime.now())
                .build();
    }

    private CategoriaRequest crearRequestValido(String nombre) {
        CategoriaRequest request = new CategoriaRequest(); // DTO de entrada usado por el service.
        request.setNombre(nombre);
        request.setDescripcion("Descripcion de prueba");
        request.setActivo(true);
        return request;
    }

    @Test // Caso: listar todas las categorias sin filtros.
    void listarCategorias_debeRetornarTodasLasCategorias() {
        // GIVEN: el repositorio devuelve dos categorias.
        when(categoriaRepository.findAll()).thenReturn(
                List.of(crearCategoriaModel(1L, "Bebidas", true), crearCategoriaModel(2L, "Postres", false)));

        // WHEN: se ejecuta el metodo real del service.
        List<CategoriaResponse> resultado = categoriaService.listarCategorias();

        // THEN: se espera el mismo tamano y datos mapeados correctamente.
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getNombre()).isEqualTo("Bebidas");
        verify(categoriaRepository).findAll();
    }

    @Test // Caso: listar solo categorias activas.
    void listarCategoriasActivas_debeRetornarSoloLasActivas() {
        // GIVEN.
        when(categoriaRepository.findByActivoTrue()).thenReturn(List.of(crearCategoriaModel(1L, "Bebidas", true)));

        // WHEN.
        List<CategoriaResponse> resultado = categoriaService.listarCategoriasActivas();

        // THEN.
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getActivo()).isTrue();
        verify(categoriaRepository).findByActivoTrue();
    }

    @Test // Caso: busqueda por id existente.
    void buscarPorId_debeRetornarCategoriaCuandoExiste() {
        // GIVEN.
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(crearCategoriaModel(1L, "Bebidas", true)));

        // WHEN.
        CategoriaResponse resultado = categoriaService.buscarPorId(1L);

        // THEN.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Bebidas");
    }

    @Test // Caso: busqueda por id inexistente debe lanzar la excepcion de negocio.
    void buscarPorId_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> categoriaService.buscarPorId(99L))
                .isInstanceOf(CategoriaNoEncontradaException.class)
                .hasMessageContaining("99");
    }

    @Test // Caso: creacion exitosa cuando el nombre no esta repetido.
    void crearCategoria_debeCrearCuandoNombreNoExiste() {
        // GIVEN.
        CategoriaRequest request = crearRequestValido("Bebidas");
        when(categoriaRepository.findByNombreIgnoreCase("Bebidas")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(CategoriaModel.class)))
                .thenReturn(crearCategoriaModel(1L, "Bebidas", true));

        // WHEN.
        CategoriaResponse resultado = categoriaService.crearCategoria(request);

        // THEN.
        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCategoria()).isEqualTo(1L);
        verify(categoriaRepository).save(any(CategoriaModel.class));
    }

    @Test // Caso: creacion fallida porque el nombre ya existe.
    void crearCategoria_debeLanzarExcepcionCuandoNombreDuplicado() {
        // GIVEN.
        CategoriaRequest request = crearRequestValido("Bebidas");
        when(categoriaRepository.findByNombreIgnoreCase("Bebidas"))
                .thenReturn(Optional.of(crearCategoriaModel(5L, "Bebidas", true)));

        // WHEN + THEN.
        assertThatThrownBy(() -> categoriaService.crearCategoria(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bebidas");
        verify(categoriaRepository, never()).save(any(CategoriaModel.class));
    }

    @Test // Caso: actualizacion exitosa de una categoria existente.
    void actualizarCategoria_debeActualizarCuandoExiste() {
        // GIVEN.
        CategoriaModel existente = crearCategoriaModel(1L, "Bebidas", true);
        CategoriaRequest request = crearRequestValido("Bebidas Frias");
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.findByNombreIgnoreCase("Bebidas Frias")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(CategoriaModel.class))).thenReturn(existente);

        // WHEN.
        CategoriaResponse resultado = categoriaService.actualizarCategoria(1L, request);

        // THEN.
        assertThat(resultado.getNombre()).isEqualTo("Bebidas Frias");
        verify(categoriaRepository).save(existente);
    }

    @Test // Caso: actualizacion fallida cuando la categoria no existe.
    void actualizarCategoria_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> categoriaService.actualizarCategoria(99L, crearRequestValido("X")))
                .isInstanceOf(CategoriaNoEncontradaException.class);
        verify(categoriaRepository, never()).save(any(CategoriaModel.class));
    }

    @Test // Caso: cambio de estado activo/inactivo.
    void cambiarEstadoCategoria_debeActualizarElEstado() {
        // GIVEN.
        CategoriaModel existente = crearCategoriaModel(1L, "Bebidas", true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepository.save(any(CategoriaModel.class))).thenReturn(existente);

        // WHEN.
        CategoriaResponse resultado = categoriaService.cambiarEstadoCategoria(1L, false);

        // THEN.
        assertThat(resultado.getActivo()).isFalse();
        verify(categoriaRepository).save(existente);
    }

    @Test // Caso: eliminacion exitosa de una categoria existente.
    void eliminarCategoria_debeEliminarCuandoExiste() {
        // GIVEN.
        CategoriaModel existente = crearCategoriaModel(1L, "Bebidas", true);
        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(existente));

        // WHEN.
        categoriaService.eliminarCategoria(1L);

        // THEN.
        verify(categoriaRepository).delete(existente);
    }

    @Test // Caso: eliminacion fallida cuando la categoria no existe.
    void eliminarCategoria_debeLanzarExcepcionCuandoNoExiste() {
        // GIVEN.
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        // WHEN + THEN.
        assertThatThrownBy(() -> categoriaService.eliminarCategoria(99L))
                .isInstanceOf(CategoriaNoEncontradaException.class);
        verify(categoriaRepository, never()).delete(any(CategoriaModel.class));
    }
}
