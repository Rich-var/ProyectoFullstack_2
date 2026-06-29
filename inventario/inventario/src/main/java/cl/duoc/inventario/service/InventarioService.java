package cl.duoc.inventario.service;

import cl.duoc.inventario.dto.request.InventarioRequest;
import cl.duoc.inventario.dto.response.InventarioResponse;
import cl.duoc.inventario.exception.InventarioNoEncontradoException;
import cl.duoc.inventario.model.InventarioModel;
import cl.duoc.inventario.repository.InventarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {

    private final InventarioRepository inventarioRepository;

    public InventarioResponse obtenerStock(Long idProducto, Long idSucursal) {
        log.info("Consultando stock para producto ID: {} en sucursal ID: {}", idProducto, idSucursal);
        
        InventarioModel inventario = inventarioRepository.findByIdProductoAndIdSucursal(idProducto, idSucursal)
                .orElseThrow(() -> new InventarioNoEncontradoException(
                        "No se encontró registro de inventario para el producto " + idProducto + " en la sucursal " + idSucursal));
                        
        return mapearAResponse(inventario);
    }

    public InventarioResponse actualizarStock(InventarioRequest request) {
        log.info("Actualizando stock para producto ID: {} en sucursal ID: {} a cantidad: {}", 
                request.getIdProducto(), request.getIdSucursal(), request.getCantidad());

        InventarioModel inventario = inventarioRepository.findByIdProductoAndIdSucursal(request.getIdProducto(), request.getIdSucursal())
                .orElse(InventarioModel.builder()
                        .idProducto(request.getIdProducto())
                        .idSucursal(request.getIdSucursal())
                        .build());

        inventario.setCantidad(request.getCantidad());
        InventarioModel guardado = inventarioRepository.save(inventario);

        log.info("Stock actualizado correctamente. ID Inventario: {}", guardado.getIdInventario());
        return mapearAResponse(guardado);
    }

    private InventarioResponse mapearAResponse(InventarioModel inventario) {
        return InventarioResponse.builder()
                .idInventario(inventario.getIdInventario())
                .idProducto(inventario.getIdProducto())
                .idSucursal(inventario.getIdSucursal())
                .cantidad(inventario.getCantidad())
                .fechaActualizacion(inventario.getFechaActualizacion())
                .build();
    }
}