package cl.duoc.proveedores.service;

import cl.duoc.proveedores.dto.request.ProveedorRequest;
import cl.duoc.proveedores.dto.response.ProveedorResponse;
import cl.duoc.proveedores.exception.ProveedorNoEncontradoException;
import cl.duoc.proveedores.model.ProveedorModel;
import cl.duoc.proveedores.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public List<ProveedorResponse> listarProveedores() {
        log.info("Listando todos los proveedores");

        return proveedorRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ProveedorResponse> listarProveedoresActivos() {
        log.info("Listando proveedores activos");

        return proveedorRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ProveedorResponse> listarPorRubro(String rubro) {
        log.info("Listando proveedores por rubro: {}", rubro);

        return proveedorRepository.findByRubroIgnoreCase(rubro)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ProveedorResponse buscarPorId(Long idProveedor) {
        log.info("Buscando proveedor con ID: {}", idProveedor);

        ProveedorModel proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado con ID: " + idProveedor));

        return mapearAResponse(proveedor);
    }

    public ProveedorResponse crearProveedor(ProveedorRequest request) {
        log.info("Creando proveedor con nombre: {}", request.getNombre());

        proveedorRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(proveedor -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con el nombre: " + request.getNombre());
                });

        proveedorRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(proveedor -> {
                    throw new IllegalArgumentException("Ya existe un proveedor con el email: " + request.getEmail());
                });

        ProveedorModel proveedor = ProveedorModel.builder()
                .nombre(request.getNombre())
                .rubro(request.getRubro())
                .telefono(request.getTelefono())
                .email(request.getEmail())
                .direccion(request.getDireccion())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        ProveedorModel proveedorGuardado = proveedorRepository.save(proveedor);

        log.info("Proveedor creado correctamente con ID: {}", proveedorGuardado.getIdProveedor());

        return mapearAResponse(proveedorGuardado);
    }

    public ProveedorResponse actualizarProveedor(Long idProveedor, ProveedorRequest request) {
        log.info("Actualizando proveedor con ID: {}", idProveedor);

        ProveedorModel proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado con ID: " + idProveedor));

        proveedorRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(proveedorExistente -> {
                    if (!proveedorExistente.getIdProveedor().equals(idProveedor)) {
                        throw new IllegalArgumentException("Ya existe otro proveedor con el nombre: " + request.getNombre());
                    }
                });

        proveedorRepository.findByEmailIgnoreCase(request.getEmail())
                .ifPresent(proveedorExistente -> {
                    if (!proveedorExistente.getIdProveedor().equals(idProveedor)) {
                        throw new IllegalArgumentException("Ya existe otro proveedor con el email: " + request.getEmail());
                    }
                });

        proveedor.setNombre(request.getNombre());
        proveedor.setRubro(request.getRubro());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());

        if (request.getActivo() != null) {
            proveedor.setActivo(request.getActivo());
        }

        ProveedorModel proveedorActualizado = proveedorRepository.save(proveedor);

        log.info("Proveedor actualizado correctamente con ID: {}", proveedorActualizado.getIdProveedor());

        return mapearAResponse(proveedorActualizado);
    }

    public ProveedorResponse cambiarEstadoProveedor(Long idProveedor, Boolean activo) {
        log.info("Cambiando estado de proveedor ID: {} a activo: {}", idProveedor, activo);

        ProveedorModel proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado con ID: " + idProveedor));

        proveedor.setActivo(activo);

        ProveedorModel proveedorActualizado = proveedorRepository.save(proveedor);

        return mapearAResponse(proveedorActualizado);
    }

    public void eliminarProveedor(Long idProveedor) {
        log.info("Eliminando proveedor con ID: {}", idProveedor);

        ProveedorModel proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ProveedorNoEncontradoException("Proveedor no encontrado con ID: " + idProveedor));

        proveedorRepository.delete(proveedor);

        log.info("Proveedor eliminado correctamente con ID: {}", idProveedor);
    }

    private ProveedorResponse mapearAResponse(ProveedorModel proveedor) {
        return ProveedorResponse.builder()
                .idProveedor(proveedor.getIdProveedor())
                .nombre(proveedor.getNombre())
                .rubro(proveedor.getRubro())
                .telefono(proveedor.getTelefono())
                .email(proveedor.getEmail())
                .direccion(proveedor.getDireccion())
                .activo(proveedor.getActivo())
                .fechaCreacion(proveedor.getFechaCreacion())
                .build();
    }
}