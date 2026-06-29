CREATE TABLE IF NOT EXISTS inventarios (
    id_inventario BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_producto BIGINT NOT NULL,
    id_sucursal BIGINT NOT NULL,
    cantidad INT NOT NULL,
    fecha_actualizacion DATETIME NOT NULL,
    UNIQUE KEY uq_producto_sucursal (id_producto, id_sucursal)
);