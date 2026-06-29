package cl.duoc.sucursales.service;

import cl.duoc.sucursales.dto.request.SucursalRequest;
import cl.duoc.sucursales.dto.response.SucursalResponse;
import cl.duoc.sucursales.exception.SucursalNoEncontradaException;
import cl.duoc.sucursales.model.SucursalModel;
import cl.duoc.sucursales.repository.SucursalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SucursalService {

    private final SucursalRepository sucursalRepository;

    public List<SucursalResponse> listarSucursales() {
        log.info("Listando todas las sucursales");

        return sucursalRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<SucursalResponse> listarSucursalesActivas() {
        log.info("Listando sucursales activas");

        return sucursalRepository.findByActivoTrue()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<SucursalResponse> listarPorComuna(String comuna) {
        log.info("Listando sucursales por comuna: {}", comuna);

        return sucursalRepository.findByComunaIgnoreCase(comuna)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public SucursalResponse buscarPorId(Long idSucursal) {
        log.info("Buscando sucursal con ID: {}", idSucursal);

        SucursalModel sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new SucursalNoEncontradaException("Sucursal no encontrada con ID: " + idSucursal));

        return mapearAResponse(sucursal);
    }

    public SucursalResponse crearSucursal(SucursalRequest request) {
        log.info("Creando sucursal con nombre: {}", request.getNombre());

        validarHorario(request.getHorarioApertura(), request.getHorarioCierre());

        sucursalRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(sucursal -> {
                    throw new IllegalArgumentException("Ya existe una sucursal con el nombre: " + request.getNombre());
                });

        SucursalModel sucursal = SucursalModel.builder()
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .comuna(request.getComuna())
                .telefono(request.getTelefono())
                .horarioApertura(request.getHorarioApertura())
                .horarioCierre(request.getHorarioCierre())
                .activo(request.getActivo() != null ? request.getActivo() : true)
                .build();

        SucursalModel sucursalGuardada = sucursalRepository.save(sucursal);

        log.info("Sucursal creada correctamente con ID: {}", sucursalGuardada.getIdSucursal());

        return mapearAResponse(sucursalGuardada);
    }

    public SucursalResponse actualizarSucursal(Long idSucursal, SucursalRequest request) {
        log.info("Actualizando sucursal con ID: {}", idSucursal);

        validarHorario(request.getHorarioApertura(), request.getHorarioCierre());

        SucursalModel sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new SucursalNoEncontradaException("Sucursal no encontrada con ID: " + idSucursal));

        sucursalRepository.findByNombreIgnoreCase(request.getNombre())
                .ifPresent(sucursalExistente -> {
                    if (!sucursalExistente.getIdSucursal().equals(idSucursal)) {
                        throw new IllegalArgumentException("Ya existe otra sucursal con el nombre: " + request.getNombre());
                    }
                });

        sucursal.setNombre(request.getNombre());
        sucursal.setDireccion(request.getDireccion());
        sucursal.setComuna(request.getComuna());
        sucursal.setTelefono(request.getTelefono());
        sucursal.setHorarioApertura(request.getHorarioApertura());
        sucursal.setHorarioCierre(request.getHorarioCierre());

        if (request.getActivo() != null) {
            sucursal.setActivo(request.getActivo());
        }

        SucursalModel sucursalActualizada = sucursalRepository.save(sucursal);

        log.info("Sucursal actualizada correctamente con ID: {}", sucursalActualizada.getIdSucursal());

        return mapearAResponse(sucursalActualizada);
    }

    public SucursalResponse cambiarEstadoSucursal(Long idSucursal, Boolean activo) {
        log.info("Cambiando estado de sucursal ID: {} a activo: {}", idSucursal, activo);

        SucursalModel sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new SucursalNoEncontradaException("Sucursal no encontrada con ID: " + idSucursal));

        sucursal.setActivo(activo);

        SucursalModel sucursalActualizada = sucursalRepository.save(sucursal);

        return mapearAResponse(sucursalActualizada);
    }

    public void eliminarSucursal(Long idSucursal) {
        log.info("Eliminando sucursal con ID: {}", idSucursal);

        SucursalModel sucursal = sucursalRepository.findById(idSucursal)
                .orElseThrow(() -> new SucursalNoEncontradaException("Sucursal no encontrada con ID: " + idSucursal));

        sucursalRepository.delete(sucursal);

        log.info("Sucursal eliminada correctamente con ID: {}", idSucursal);
    }

    private void validarHorario(String apertura, String cierre) {
        LocalTime horaApertura = LocalTime.parse(apertura);
        LocalTime horaCierre = LocalTime.parse(cierre);

        if (!horaCierre.isAfter(horaApertura)) {
            throw new IllegalArgumentException("El horario de cierre debe ser posterior al horario de apertura");
        }
    }

    private SucursalResponse mapearAResponse(SucursalModel sucursal) {
        return SucursalResponse.builder()
                .idSucursal(sucursal.getIdSucursal())
                .nombre(sucursal.getNombre())
                .direccion(sucursal.getDireccion())
                .comuna(sucursal.getComuna())
                .telefono(sucursal.getTelefono())
                .horarioApertura(sucursal.getHorarioApertura())
                .horarioCierre(sucursal.getHorarioCierre())
                .activo(sucursal.getActivo())
                .fechaCreacion(sucursal.getFechaCreacion())
                .build();
    }
}