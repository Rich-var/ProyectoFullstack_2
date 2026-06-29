package cl.duoc.pedidos.exception;

/**
 * Se lanza cuando se busca, actualiza o elimina un pedido por ID
 * y este no existe en la base de datos local de Pedidos.
 */
public class PedidoNoEncontradoException extends RuntimeException {

    public PedidoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
