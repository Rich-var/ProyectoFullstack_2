package cl.duoc.empleados.controller;

import cl.duoc.empleados.dto.request.EmpleadoRequest;
import cl.duoc.empleados.dto.response.EmpleadoResponse;
import cl.duoc.empleados.service.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    @GetMapping
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleados() {
        return ResponseEntity.ok(empleadoService.listarEmpleados());
    }

    @GetMapping("/activos")
    public ResponseEntity<List<EmpleadoResponse>> listarEmpleadosActivos() {
        return ResponseEntity.ok(empleadoService.listarEmpleadosActivos());
    }

    @GetMapping("/cargo/{cargo}")
    public ResponseEntity<List<EmpleadoResponse>> listarPorCargo(@PathVariable String cargo) {
        return ResponseEntity.ok(empleadoService.listarPorCargo(cargo));
    }

    @GetMapping("/sucursal/{idSucursal}")
    public ResponseEntity<List<EmpleadoResponse>> listarPorSucursal(@PathVariable Long idSucursal) {
        return ResponseEntity.ok(empleadoService.listarPorSucursal(idSucursal));
    }

    @GetMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponse> buscarPorId(@PathVariable Long idEmpleado) {
        return ResponseEntity.ok(empleadoService.buscarPorId(idEmpleado));
    }

    @PostMapping
    public ResponseEntity<EmpleadoResponse> crearEmpleado(@Valid @RequestBody EmpleadoRequest request) {
        EmpleadoResponse response = empleadoService.crearEmpleado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idEmpleado}")
    public ResponseEntity<EmpleadoResponse> actualizarEmpleado(
            @PathVariable Long idEmpleado,
            @Valid @RequestBody EmpleadoRequest request
    ) {
        return ResponseEntity.ok(empleadoService.actualizarEmpleado(idEmpleado, request));
    }

    @PatchMapping("/{idEmpleado}/estado")
    public ResponseEntity<EmpleadoResponse> cambiarEstadoEmpleado(
            @PathVariable Long idEmpleado,
            @RequestParam Boolean activo
    ) {
        return ResponseEntity.ok(empleadoService.cambiarEstadoEmpleado(idEmpleado, activo));
    }

    @DeleteMapping("/{idEmpleado}")
    public ResponseEntity<Void> eliminarEmpleado(@PathVariable Long idEmpleado) {
        empleadoService.eliminarEmpleado(idEmpleado);
        return ResponseEntity.noContent().build();
    }
}