package cl.duoc.empleados.repository;

import cl.duoc.empleados.model.EmpleadoModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository extends JpaRepository<EmpleadoModel, Long> {

    Optional<EmpleadoModel> findByRutIgnoreCase(String rut);

    Optional<EmpleadoModel> findByEmailIgnoreCase(String email);

    List<EmpleadoModel> findByActivoTrue();

    List<EmpleadoModel> findByCargoIgnoreCase(String cargo);

    List<EmpleadoModel> findByIdSucursal(Long idSucursal);
}