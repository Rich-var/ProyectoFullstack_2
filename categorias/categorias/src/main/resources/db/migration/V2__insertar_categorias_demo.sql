INSERT INTO categorias (nombre, descripcion, activo, fecha_creacion) 
VALUES 
('Cafés', 'Variedades de café de grano y especialidades', TRUE, NOW()),
('Pastelería', 'Tortas, kuchenes y bollería fresca', TRUE, NOW()),
('Bebidas Frías', 'Jugos naturales, tés helados y bebidas', TRUE, NOW())
ON DUPLICATE KEY UPDATE nombre=nombre;