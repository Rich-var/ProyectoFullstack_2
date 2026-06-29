package cl.duoc.pedidos.repository;

import cl.duoc.pedidos.model.PedidoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<PedidoModel, Long> {
    List<PedidoModel> findByIdUsuario(Long idUsuario);
    List<PedidoModel> findByEstadoIgnoreCase(String estado);
}