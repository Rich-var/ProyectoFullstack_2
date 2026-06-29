CREATE TABLE IF NOT EXISTS productos (
    id_producto   BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre        VARCHAR(100)  NOT NULL,
    descripcion   VARCHAR(255),
    precio        INT           NOT NULL,
    activo        BOOLEAN       NOT NULL,
    fecha_creacion DATETIME     NOT NULL
);
