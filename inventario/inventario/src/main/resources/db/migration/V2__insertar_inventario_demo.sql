INSERT INTO inventarios (id_producto, id_sucursal, cantidad, fecha_actualizacion)
VALUES 
(1, 1, 50, NOW()),
(2, 1, 30, NOW()),
(3, 2, 100, NOW())
ON DUPLICATE KEY UPDATE cantidad=cantidad;