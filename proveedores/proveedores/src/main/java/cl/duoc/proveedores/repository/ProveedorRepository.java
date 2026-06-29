package cl.duoc.proveedores.repository;

import cl.duoc.proveedores.model.ProveedorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<ProveedorModel, Long> {

    Optional<ProveedorModel> findByNombreIgnoreCase(String nombre);

    Optional<ProveedorModel> findByEmailIgnoreCase(String email);

    List<ProveedorModel> findByActivoTrue();

    List<ProveedorModel> findByRubroIgnoreCase(String rubro);
}