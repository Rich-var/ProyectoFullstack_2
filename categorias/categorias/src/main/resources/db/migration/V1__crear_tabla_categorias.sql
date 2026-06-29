CREATE TABLE IF NOT EXISTS categorias (
    id_categoria BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL,
    fecha_creacion DATETIME NOT NULL
);