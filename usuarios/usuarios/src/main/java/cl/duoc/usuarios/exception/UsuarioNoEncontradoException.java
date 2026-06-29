package cl.duoc.usuarios.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}