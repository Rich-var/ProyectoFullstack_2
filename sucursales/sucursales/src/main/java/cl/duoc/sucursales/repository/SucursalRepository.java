package cl.duoc.sucursales.repository;

import cl.duoc.sucursales.model.SucursalModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SucursalRepository extends JpaRepository<SucursalModel, Long> {

    Optional<SucursalModel> findByNombreIgnoreCase(String nombre);

    List<SucursalModel> findByActivoTrue();

    List<SucursalModel> findByComunaIgnoreCase(String comuna);
}