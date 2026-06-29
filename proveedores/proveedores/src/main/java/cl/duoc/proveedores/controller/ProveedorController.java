package cl.duoc.proveedores.controller;

import cl.duoc.proveedores.dto.request.ProveedorRequest;
import cl.duoc.proveedores.dto.response.ProveedorResponse;
import cl.duoc.proveedores.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> listarProveedores() {
        return ResponseEntity.ok(proveedorService.listarProveedores());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<ProveedorResponse>> listarProveedoresActivos() {
        return ResponseEntity.ok(proveedorService.listarProveedoresActivos());
    }

    @GetMapping("/rubro/{rubro}")
    public ResponseEntity<List<ProveedorResponse>> listarPorRubro(@PathVariable String rubro) {
        return ResponseEntity.ok(proveedorService.listarPorRubro(rubro));
    }

    @GetMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> buscarPorId(@PathVariable Long idProveedor) {
        return ResponseEntity.ok(proveedorService.buscarPorId(idProveedor));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> crearProveedor(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.crearProveedor(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> actualizarProveedor(
            @PathVariable Long idProveedor,
            @Valid @RequestBody ProveedorRequest request
    ) {
        return ResponseEntity.ok(proveedorService.actualizarProveedor(idProveedor, request));
    }

    @PatchMapping("/{idProveedor}/estado")
    public ResponseEntity<ProveedorResponse> cambiarEstadoProveedor(
            @PathVariable Long idProveedor,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(proveedorService.cambiarEstadoProveedor(idProveedor, activo));
    }

    @DeleteMapping("/{idProveedor}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long idProveedor) {
        proveedorService.eliminarProveedor(idProveedor);
        return ResponseEntity.noContent().build();
    }
}