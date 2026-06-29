package cl.duoc.categorias.controller;

import cl.duoc.categorias.dto.request.CategoriaRequest;
import cl.duoc.categorias.dto.response.CategoriaResponse;
import cl.duoc.categorias.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @GetMapping
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarCategorias());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<CategoriaResponse>> listarCategoriasActivas() {
        return ResponseEntity.ok(categoriaService.listarCategoriasActivas());
    }

    @GetMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponse> buscarPorId(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(categoriaService.buscarPorId(idCategoria));
    }

    @PostMapping
    public ResponseEntity<CategoriaResponse> crearCategoria(@Valid @RequestBody CategoriaRequest request) {
        CategoriaResponse response = categoriaService.crearCategoria(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idCategoria}")
    public ResponseEntity<CategoriaResponse> actualizarCategoria(
            @PathVariable Long idCategoria,
            @Valid @RequestBody CategoriaRequest request
    ) {
        return ResponseEntity.ok(categoriaService.actualizarCategoria(idCategoria, request));
    }

    @PatchMapping("/{idCategoria}/estado")
    public ResponseEntity<CategoriaResponse> cambiarEstadoCategoria(
            @PathVariable Long idCategoria,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(categoriaService.cambiarEstadoCategoria(idCategoria, activo));
    }

    @DeleteMapping("/{idCategoria}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long idCategoria) {
        categoriaService.eliminarCategoria(idCategoria);
        return ResponseEntity.noContent().build();
    }
}