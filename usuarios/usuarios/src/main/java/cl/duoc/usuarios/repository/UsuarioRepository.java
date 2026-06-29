package cl.duoc.usuarios.repository;

import cl.duoc.usuarios.model.UsuarioModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    Optional<UsuarioModel> findByUsernameIgnoreCase(String username);

    Optional<UsuarioModel> findByEmailIgnoreCase(String email);

    List<UsuarioModel> findByActivoTrue();

    List<UsuarioModel> findByRolIgnoreCase(String rol);
}