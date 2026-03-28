-- ==============================================================================
-- Archivo: init_usuarios.sql
-- Autor: Henry Wong (hwongu@gmail.com)
-- Descripción: 
-- Inicialización del dominio de Seguridad (IAM).
-- Contiene únicamente la tabla de usuarios para gestionar la autenticación y 
-- la autorización. No tiene conocimiento de las operaciones del negocio.
-- ==============================================================================

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TABLE usuario (
    id_usuario INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username CITEXT UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT TRUE
);

INSERT INTO usuario (username, password, estado) 
VALUES ('hwongu', 'clave', true);