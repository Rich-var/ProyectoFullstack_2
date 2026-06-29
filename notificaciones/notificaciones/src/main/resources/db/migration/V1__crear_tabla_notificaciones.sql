CREATE TABLE IF NOT EXISTS notificaciones (
    id_notificacion BIGINT AUTO_INCREMENT PRIMARY KEY,
    destinatario VARCHAR(120) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_envio DATETIME NOT NULL
);