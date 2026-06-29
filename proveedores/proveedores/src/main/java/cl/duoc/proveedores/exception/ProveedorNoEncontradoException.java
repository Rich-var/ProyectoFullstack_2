package cl.duoc.proveedores.exception;

public class ProveedorNoEncontradoException extends RuntimeException {

    public ProveedorNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}