package cl.duoc.categorias.service;

import cl.duoc.categorias.dto.request.CategoriaRequest;
import cl.duoc.categorias.dto.response.CategoriaResponse;
import cl.duoc.categorias.exception.CategoriaNoEncontradaException;
import cl.duoc.categorias.model.CategoriaModel;
import cl.duoc.categorias.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaResponse> listarCategorias() {
        log.info("Listando todas las categorias");

        return categoriaRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<CategoriaResponse> listarCategoriasActivas() {
        log.info("Listando categorias activas");

        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public CategoriaResponse buscarPorId(Long idCategoria) {
        log.info("Buscando categoria con ID: {}", idCategoria);

        CategoriaModel categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con ID: " + idCategoria));

        return mapearAResponse(categoria);
    }

    public CategoriaResponse crearCategoria(CategoriaRequest request) {
        log.info("Creando categoria con nombre: {}", request.getNombre());

        categoriaRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(categoria -> {
                    throw new IllegalArgumentException("Ya existe una categoria con el nombre: " + request.getNombre());
                });

        CategoriaModel categoria = CategoriaModel.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        CategoriaModel categoriaGuardada = categoriaRepository.save(categoria);

        log.info("Categoria creada correctamente con ID: {}", categoriaGuardada.getIdCategoria());

        return mapearAResponse(categoriaGuardada);
    }

    public CategoriaResponse actualizarCategoria(Long idCategoria, CategoriaRequest request) {
        log.info("Actualizando categoria con ID: {}", idCategoria);

        CategoriaModel categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con ID: " + idCategoria));

        categoriaRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(categoriaExistente -> {
                    if (!categoriaExistente.getIdCategoria().equals(idCategoria)) {
                        throw new IllegalArgumentException("Ya existe otra categoria con el nombre: " + request.getNombre());
                    }
                });

        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());

        if (request.getActivo() != null) {
            categoria.setActivo(request.getActivo());
        }

        CategoriaModel categoriaActualizada = categoriaRepository.save(categoria);

        log.info("Categoria actualizada correctamente con ID: {}", categoriaActualizada.getIdCategoria());

        return mapearAResponse(categoriaActualizada);
    }

    public CategoriaResponse cambiarEstadoCategoria(Long idCategoria, Boolean activo) {
        log.info("Cambiando estado de categoria ID: {} a activo: {}", idCategoria, activo);

        CategoriaModel categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con ID: " + idCategoria));

        categoria.setActivo(activo);

        CategoriaModel categoriaActualizada = categoriaRepository.save(categoria);

        return mapearAResponse(categoriaActualizada);
    }

    public void eliminarCategoria(Long idCategoria) {
        log.info("Eliminando categoria con ID: {}", idCategoria);

        CategoriaModel categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new CategoriaNoEncontradaException("Categoria no encontrada con ID: " + idCategoria));

        categoriaRepository.delete(categoria);

        log.info("Categoria eliminada correctamente con ID: {}", idCategoria);
    }

    private CategoriaResponse mapearAResponse(CategoriaModel categoria) {
        return CategoriaResponse.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .descripcion(categoria.getDescripcion())
                .activo(categoria.getActivo())
                .fechaCreacion(categoria.getFechaCreacion())
                .build();
    }
}