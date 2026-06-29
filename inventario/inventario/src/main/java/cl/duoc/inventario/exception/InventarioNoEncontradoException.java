package cl.duoc.inventario.exception;

public class InventarioNoEncontradoException extends RuntimeException {

    public InventarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}