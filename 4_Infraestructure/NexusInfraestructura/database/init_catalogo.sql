-- ==============================================================================
-- Archivo: init_catalogo.sql
-- Autor: Henry Wong (hwongu@gmail.com)
-- Descripción: 
-- Inicialización del dominio de Catálogo de Productos.
-- Se mantiene la restricción de llave foránea (FOREIGN KEY) entre producto 
-- y categoría porque ambas entidades coexisten en la misma base de datos.
-- ==============================================================================

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE categoria (
    id_categoria INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre CITEXT NOT NULL,
    descripcion TEXT
);

CREATE TABLE producto (
    id_producto INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_categoria INT NOT NULL,
    nombre CITEXT NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    -- Integridad referencial interna:
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);

INSERT INTO categoria (nombre, descripcion) 
VALUES 
('Laptops', 'Computadoras portátiles de alto rendimiento'),
('Periféricos', 'Teclados, ratones y accesorios');

INSERT INTO producto (id_categoria, nombre, precio, stock) 
VALUES 
(1, 'Laptop Lenovo ThinkPad T14', 1200.50, 50),
(1, 'MacBook Pro M3', 2500.00, 20),
(2, 'Mouse Inalámbrico Logitech MX Master 3', 99.99, 100);