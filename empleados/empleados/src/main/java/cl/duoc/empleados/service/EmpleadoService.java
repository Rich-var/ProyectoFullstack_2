package cl.duoc.empleados.service;

import cl.duoc.empleados.dto.request.EmpleadoRequest;
import cl.duoc.empleados.dto.response.EmpleadoResponse;
import cl.duoc.empleados.exception.EmpleadoNoEncontradoException;
import cl.duoc.empleados.model.EmpleadoModel;
import cl.duoc.empleados.repository.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    public List<EmpleadoResponse> listarEmpleados() {
        log.info("Listando todos los empleados");

        return empleadoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<EmpleadoResponse> listarEmpleadosActivos() {
        log.info("Listando empleados activos");

        return empleadoRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<EmpleadoResponse> listarPorCargo(String cargo) {
        log.info("Listando empleados por cargo: {}", cargo);

        return empleadoRepository.findByCargoIgnoreCase(cargo)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<EmpleadoResponse> listarPorSucursal(Long idSucursal) {
        log.info("Listando empleados por sucursal ID: {}", idSucursal);

        return empleadoRepository.findByIdSucursal(idSucursal)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public EmpleadoResponse buscarPorId(Long idEmpleado) {
        log.info("Buscando empleado con ID: {}", idEmpleado);

        EmpleadoModel empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + idEmpleado));

        return mapearAResponse(empleado);
    }

    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        log.info("Creando empleado con RUT: {}", request.getRut());

        empleadoRepository.findByRutIgnoreCase(request.getRut())
                .ifPresent(empleado -> {
                    throw new IllegalArgumentException("Ya existe un empleado con el RUT: " + request.getRut());
                });

        empleadoRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(empleado -> {
                    throw new IllegalArgumentException("Ya existe un empleado con el email: " + request.getEmail());
                });

        EmpleadoModel empleado = EmpleadoModel.builder()
                .rut(request.getRut())
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .cargo(request.getCargo())
                .idSucursal(request.getIdSucursal())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        EmpleadoModel empleadoGuardado = empleadoRepository.save(empleado);

        log.info("Empleado creado correctamente con ID: {}", empleadoGuardado.getIdEmpleado());

        return mapearAResponse(empleadoGuardado);
    }

    public EmpleadoResponse actualizarEmpleado(Long idEmpleado, EmpleadoRequest request) {
        log.info("Actualizando empleado con ID: {}", idEmpleado);

        EmpleadoModel empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + idEmpleado));

        empleadoRepository.findByRutIgnoreCase(request.getRut())
                .ifPresent(empleadoExistente -> {
                    if (!empleadoExistente.getIdEmpleado().equals(idEmpleado)) {
                        throw new IllegalArgumentException("Ya existe otro empleado con el RUT: " + request.getRut());
                    }
                });

        empleadoRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(empleadoExistente -> {
                    if (!empleadoExistente.getIdEmpleado().equals(idEmpleado)) {
                        throw new IllegalArgumentException("Ya existe otro empleado con el email: " + request.getEmail());
                    }
                });

        empleado.setRut(request.getRut());
        empleado.setNombres(request.getNombres());
        empleado.setApellidos(request.getApellidos());
        empleado.setEmail(request.getEmail());
        empleado.setTelefono(request.getTelefono());
        empleado.setCargo(request.getCargo());
        empleado.setIdSucursal(request.getIdSucursal());

        if (request.getActivo() != null) {
            empleado.setActivo(request.getActivo());
        }

        EmpleadoModel empleadoActualizado = empleadoRepository.save(empleado);

        log.info("Empleado actualizado correctamente con ID: {}", empleadoActualizado.getIdEmpleado());

        return mapearAResponse(empleadoActualizado);
    }

    public EmpleadoResponse cambiarEstadoEmpleado(Long idEmpleado, Boolean activo) {
        log.info("Cambiando estado de empleado ID: {} a activo: {}", idEmpleado, activo);

        EmpleadoModel empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + idEmpleado));

        empleado.setActivo(activo);

        EmpleadoModel empleadoActualizado = empleadoRepository.save(empleado);

        return mapearAResponse(empleadoActualizado);
    }

    public void eliminarEmpleado(Long idEmpleado) {
        log.info("Eliminando empleado con ID: {}", idEmpleado);

        EmpleadoModel empleado = empleadoRepository.findById(idEmpleado)
                .orElseThrow(() -> new EmpleadoNoEncontradoException("Empleado no encontrado con ID: " + idEmpleado));

        empleadoRepository.delete(empleado);

        log.info("Empleado eliminado correctamente con ID: {}", idEmpleado);
    }

    private EmpleadoResponse mapearAResponse(EmpleadoModel empleado) {
        return EmpleadoResponse.builder()
                .idEmpleado(empleado.getIdEmpleado())
                .rut(empleado.getRut())
                .nombres(empleado.getNombres())
                .apellidos(empleado.getApellidos())
                .email(empleado.getEmail())
                .telefono(empleado.getTelefono())
                .cargo(empleado.getCargo())
                .idSucursal(empleado.getIdSucursal())
                .activo(empleado.getActivo())
                .fechaCreacion(empleado.getFechaCreacion())
                .build();
    }
}