CREATE TABLE IF NOT EXISTS proveedores (
    id_proveedor BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    rubro VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL,
    fecha_creacion DATETIME NOT NULL
);