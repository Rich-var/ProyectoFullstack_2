package cl.duoc.productos.controller;

import cl.duoc.productos.dto.request.ProductoRequest;
import cl.duoc.productos.dto.response.ProductoResponse;
import cl.duoc.productos.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        return ResponseEntity.ok(productoService.listarProductos());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProductoResponse>> listarProductosActivos() {
        return ResponseEntity.ok(productoService.listarProductosActivos());
    }

    @GetMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable Long idProducto) {
        return ResponseEntity.ok(productoService.buscarPorId(idProducto));
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idProducto}")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable Long idProducto,
            @Valid @RequestBody ProductoRequest request
    ) {
        return ResponseEntity.ok(productoService.actualizarProducto(idProducto, request));
    }

    @PatchMapping("/{idProducto}/estado")
    public ResponseEntity<ProductoResponse> cambiarEstadoProducto(
            @PathVariable Long idProducto,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(productoService.cambiarEstadoProducto(idProducto, activo));
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Void> eliminarProducto(@PathVariable Long idProducto) {
        productoService.eliminarProducto(idProducto);
        return ResponseEntity.noContent().build();
    }
}