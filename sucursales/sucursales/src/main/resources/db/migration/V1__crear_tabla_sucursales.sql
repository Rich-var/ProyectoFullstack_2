CREATE TABLE IF NOT EXISTS sucursales (
    id_sucursal BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    direccion VARCHAR(255) NOT NULL,
    comuna VARCHAR(80) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    horario_apertura VARCHAR(50) NOT NULL,
    horario_cierre VARCHAR(50) NOT NULL,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATETIME NOT NULL
);