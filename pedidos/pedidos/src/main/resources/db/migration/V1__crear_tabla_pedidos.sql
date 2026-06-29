CREATE TABLE IF NOT EXISTS pedidos (
    id_pedido BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    id_producto BIGINT NOT NULL,
    cantidad INT NOT NULL,
    total INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    fecha_creacion DATETIME NOT NULL
);