package cl.duoc.categorias.repository;

import cl.duoc.categorias.model.CategoriaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<CategoriaModel, Long> {

    Optional<CategoriaModel> findByNombreIgnoreCase(String nombre);

    List<CategoriaModel> findByActivoTrue();
}