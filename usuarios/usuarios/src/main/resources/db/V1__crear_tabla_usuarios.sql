CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    rol VARCHAR(50) NOT NULL,
    id_empleado BIGINT,
    activo BOOLEAN NOT NULL,
    fecha_creacion DATETIME NOT NULL
);