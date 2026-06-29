package cl.duoc.productos.repository;

import cl.duoc.productos.model.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoModel, Long> {

    Optional<ProductoModel> findByNombreIgnoreCase(String nombre);

    List<ProductoModel> findByActivoTrue();
}