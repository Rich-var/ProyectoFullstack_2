package cl.duoc.inventario.repository;

import cl.duoc.inventario.model.InventarioModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<InventarioModel, Long> {
    Optional<InventarioModel> findByIdProductoAndIdSucursal(Long idProducto, Long idSucursal);
}