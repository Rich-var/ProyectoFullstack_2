package cl.duoc.sucursales.controller;

import cl.duoc.sucursales.dto.request.SucursalRequest;
import cl.duoc.sucursales.dto.response.SucursalResponse;
import cl.duoc.sucursales.service.SucursalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sucursales")
@RequiredArgsConstructor
public class SucursalController {

    private final SucursalService sucursalService;

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listarSucursales() {
        return ResponseEntity.ok(sucursalService.listarSucursales());
    }

    @GetMapping("/activas")
    public ResponseEntity<List<SucursalResponse>> listarSucursalesActivas() {
        return ResponseEntity.ok(sucursalService.listarSucursalesActivas());
    }

    @GetMapping("/comuna/{comuna}")
    public ResponseEntity<List<SucursalResponse>> listarPorComuna(@PathVariable String comuna) {
        return ResponseEntity.ok(sucursalService.listarPorComuna(comuna));
    }

    @GetMapping("/{idSucursal}")
    public ResponseEntity<SucursalResponse> buscarPorId(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(sucursalService.buscarPorId(idSucursal));
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> crearSucursal(@Valid @RequestBody SucursalRequest request) {
        SucursalResponse response = sucursalService.crearSucursal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idSucursal}")
    public ResponseEntity<SucursalResponse> actualizarSucursal(
            @PathVariable Long idSucursal,
            @Valid @RequestBody SucursalRequest request
    ) {
        return ResponseEntity.ok(sucursalService.actualizarSucursal(idSucursal, request));
    }

    @PatchMapping("/{idSucursal}/estado")
    public ResponseEntity<SucursalResponse> cambiarEstadoSucursal(
            @PathVariable Long idSucursal,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(sucursalService.cambiarEstadoSucursal(idSucursal, activo));
    }

    @DeleteMapping("/{idSucursal}")
    public ResponseEntity<Void> eliminarSucursal(@PathVariable Long idSucursal) {
        sucursalService.eliminarSucursal(idSucursal);
        return ResponseEntity.noContent().build();
    }
}