-- ==============================================================================
-- Archivo: init_ingresos.sql
-- Autor: Henry Wong (hwongu@gmail.com)
-- Descripción: 
-- Inicialización del dominio Transaccional (Ingresos de Almacén).
-- Demuestra la implementación de "Soft References" (Referencias Lógicas).
-- Las restricciones FOREIGN KEY hacia 'usuario' y 'producto' han sido 
-- eliminadas porque esas tablas residen en otras bases de datos físicas.
-- ==============================================================================

CREATE TABLE ingreso (
    id_ingreso INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    -- Referencia Lógica: El microservicio deberá validar este ID por red (HTTP/gRPC)
    id_usuario INT NOT NULL,
    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'COMPLETADO'
);

CREATE TABLE detalle_ingreso (
    id_detalle INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_ingreso INT NOT NULL,
    -- Referencia Lógica: El microservicio deberá consultar precios al Catálogo
    id_producto INT NOT NULL,
    cantidad INT NOT NULL,
    precio_compra DECIMAL(10, 2) NOT NULL,
    -- Integridad referencial interna MANTENIDA entre cabecera y detalle:
    CONSTRAINT fk_detalle_ingreso FOREIGN KEY (id_ingreso) REFERENCES ingreso(id_ingreso)
);

INSERT INTO ingreso (id_usuario, estado) 
VALUES (1, 'COMPLETADO');

INSERT INTO detalle_ingreso (id_ingreso, id_producto, cantidad, precio_compra) 
VALUES 
(1, 1, 50, 1000.00),
(1, 2, 20, 2200.00),
(1, 3, 100, 75.00);