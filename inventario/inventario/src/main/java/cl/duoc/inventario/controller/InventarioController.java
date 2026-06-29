package cl.duoc.inventario.controller;

import cl.duoc.inventario.dto.request.InventarioRequest;
import cl.duoc.inventario.dto.response.InventarioResponse;
import cl.duoc.inventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    @GetMapping("/producto/{idProducto}/sucursal/{idSucursal}")
    public ResponseEntity<InventarioResponse> obtenerStock(
            @PathVariable Long idProducto, 
            @PathVariable Long idSucursal
    ) {
        return ResponseEntity.ok(inventarioService.obtenerStock(idProducto, idSucursal));
    }

    @PostMapping
    public ResponseEntity<InventarioResponse> actualizarStock(@Valid @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.actualizarStock(request));
    }
}