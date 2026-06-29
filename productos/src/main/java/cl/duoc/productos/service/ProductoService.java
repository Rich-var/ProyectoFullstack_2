package cl.duoc.productos.service;

import cl.duoc.productos.dto.request.ProductoRequest;
import cl.duoc.productos.dto.response.ProductoResponse;
import cl.duoc.productos.exception.ProductoNoEncontradoException;
import cl.duoc.productos.model.ProductoModel;
import cl.duoc.productos.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<ProductoResponse> listarProductos() {
        log.info("Listando todos los productos");

        return productoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ProductoResponse> listarProductosActivos() {
        log.info("Listando productos activos");

        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ProductoResponse buscarPorId(Long idProducto) {
        log.info("Buscando producto con ID: {}", idProducto);

        ProductoModel producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + idProducto));

        return mapearAResponse(producto);
    }

    public ProductoResponse crearProducto(ProductoRequest request) {
        log.info("Creando producto con nombre: {}", request.getNombre());

        productoRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(producto -> {
                    throw new IllegalArgumentException("Ya existe un producto con el nombre: " + request.getNombre());
                });

        ProductoModel producto = ProductoModel.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precio(request.getPrecio())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        ProductoModel productoGuardado = productoRepository.save(producto);

        log.info("Producto creado correctamente con ID: {}", productoGuardado.getIdProducto());

        return mapearAResponse(productoGuardado);
    }

    public ProductoResponse actualizarProducto(Long idProducto, ProductoRequest request) {
        log.info("Actualizando producto con ID: {}", idProducto);

        ProductoModel producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + idProducto));

        productoRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(productoExistente -> {
                    if (!productoExistente.getIdProducto().equals(idProducto)) {
                        throw new IllegalArgumentException("Ya existe otro producto con el nombre: " + request.getNombre());
                    }
                });

        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());

        if (request.getActivo() != null) {
            producto.setActivo(request.getActivo());
        }

        ProductoModel productoActualizado = productoRepository.save(producto);

        log.info("Producto actualizado correctamente con ID: {}", productoActualizado.getIdProducto());

        return mapearAResponse(productoActualizado);
    }

    public void eliminarProducto(Long idProducto) {
        log.info("Eliminando producto con ID: {}", idProducto);

        ProductoModel producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + idProducto));

        productoRepository.delete(producto);

        log.info("Producto eliminado correctamente con ID: {}", idProducto);
    }

    public ProductoResponse cambiarEstadoProducto(Long idProducto, Boolean activo) {
        log.info("Cambiando estado del producto ID: {} a activo: {}", idProducto, activo);

        ProductoModel producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado con ID: " + idProducto));

        producto.setActivo(activo);

        ProductoModel productoActualizado = productoRepository.save(producto);

        return mapearAResponse(productoActualizado);
    }

    private ProductoResponse mapearAResponse(ProductoModel producto) {
        return ProductoResponse.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .descripcion(producto.getDescripcion())
                .precio(producto.getPrecio())
                .activo(producto.getActivo())
                .fechaCreacion(producto.getFechaCreacion())
                .build();
    }
}