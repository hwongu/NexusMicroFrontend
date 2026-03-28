# GUIA DE PRUEBAS POSTMAN

## Microservicio
`NexusGatewayMs`

## URL base
`http://localhost:8080/NexusGateway`

## Requisito previo

Antes de probar el gateway, deben estar levantados estos microservicios:

- `NexusSeguridadMs` en `http://localhost:8081/NexusMS`
- `NexusCatalogoMs` en `http://localhost:8082/NexusMS`
- `NexusIngresoMs` en `http://localhost:8083/NexusMS`

## Seguridad

### GET /seguridad/api/usuarios
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios`
- Body
  No aplica
- Respuesta esperada
```json
[
  {
    "idUsuario": 1,
    "username": "hwongu",
    "estado": true
  }
]
```
- Codigos HTTP posibles
  `200 OK`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios`.

### GET /seguridad/api/usuarios/{id}
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios/1`
- Body
  No aplica
- Respuesta esperada
```json
{
  "idUsuario": 1,
  "username": "hwongu",
  "estado": true
}
```
- Codigos HTTP posibles
  `200 OK`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios/1`.

### POST /seguridad/api/usuarios
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios`
- Body de ejemplo
```json
{
  "username": "hwongu",
  "password": "123456",
  "estado": true
}
```
- Respuesta esperada
```json
{
  "idUsuario": 1,
  "username": "hwongu",
  "estado": true
}
```
- Codigos HTTP posibles
  `201 Created`
  `400 Bad Request`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios`.

### PUT /seguridad/api/usuarios/{id}
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios/1`
- Body de ejemplo
```json
{
  "username": "hwongu",
  "password": "nueva-clave",
  "estado": true
}
```
- Respuesta esperada
```json
{
  "message": "Usuario actualizado exitosamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios/1`.

### DELETE /seguridad/api/usuarios/{id}
- Metodo HTTP
  `DELETE`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios/1`
- Body
  No aplica
- Respuesta esperada
  Sin contenido
- Codigos HTTP posibles
  `204 No Content`
  `404 Not Found`
  `409 Conflict`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios/1`.

### POST /seguridad/api/usuarios/login
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8080/NexusGateway/seguridad/api/usuarios/login`
- Body de ejemplo
```json
{
  "username": "hwongu",
  "password": "123456"
}
```
- Respuesta esperada
```json
{
  "idUsuario": 1,
  "username": "hwongu",
  "estado": true
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `401 Unauthorized`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8081/NexusMS/api/usuarios/login`.

## Catalogo

### GET /catalogo/api/categorias
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/categorias`
- Body
  No aplica
- Respuesta esperada
```json
[
  {
    "idCategoria": 1,
    "nombre": "Monitores",
    "descripcion": "Pantallas y accesorios"
  }
]
```
- Codigos HTTP posibles
  `200 OK`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/categorias`.

### GET /catalogo/api/categorias/{id}
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/categorias/1`
- Body
  No aplica
- Respuesta esperada
```json
{
  "idCategoria": 1,
  "nombre": "Monitores",
  "descripcion": "Pantallas y accesorios"
}
```
- Codigos HTTP posibles
  `200 OK`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/categorias/1`.

### POST /catalogo/api/categorias
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/categorias`
- Body de ejemplo
```json
{
  "nombre": "Monitores",
  "descripcion": "Pantallas y accesorios"
}
```
- Respuesta esperada
```json
{
  "idCategoria": 1,
  "nombre": "Monitores",
  "descripcion": "Pantallas y accesorios"
}
```
- Codigos HTTP posibles
  `201 Created`
  `400 Bad Request`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/categorias`.

### PUT /catalogo/api/categorias/{id}
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/categorias/1`
- Body de ejemplo
```json
{
  "nombre": "Monitores Gamer",
  "descripcion": "Pantallas, soportes y accesorios"
}
```
- Respuesta esperada
```json
{
  "message": "Categoria actualizada exitosamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/categorias/1`.

### DELETE /catalogo/api/categorias/{id}
- Metodo HTTP
  `DELETE`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/categorias/1`
- Body
  No aplica
- Respuesta esperada
  Sin contenido
- Codigos HTTP posibles
  `204 No Content`
  `404 Not Found`
  `409 Conflict`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/categorias/1`.

### GET /catalogo/api/productos
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos`
- Body
  No aplica
- Respuesta esperada
```json
[
  {
    "idProducto": 1,
    "idCategoria": 1,
    "nombreCategoria": "Laptops",
    "nombre": "Laptop Lenovo ThinkPad T14",
    "precio": 1200.5,
    "stock": 50
  }
]
```
- Codigos HTTP posibles
  `200 OK`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos`.

### GET /catalogo/api/productos/{id}
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos/1`
- Body
  No aplica
- Respuesta esperada
```json
{
  "idProducto": 1,
  "idCategoria": 1,
  "nombreCategoria": "Laptops",
  "nombre": "Laptop Lenovo ThinkPad T14",
  "precio": 1200.5,
  "stock": 50
}
```
- Codigos HTTP posibles
  `200 OK`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos/1`.

### POST /catalogo/api/productos
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos`
- Body de ejemplo
```json
{
  "idCategoria": 1,
  "nombre": "Laptop Lenovo ThinkPad T14",
  "precio": 1200.5,
  "stock": 50
}
```
- Respuesta esperada
```json
{
  "idProducto": 1,
  "idCategoria": 1,
  "nombreCategoria": "Laptops",
  "nombre": "Laptop Lenovo ThinkPad T14",
  "precio": 1200.5,
  "stock": 50
}
```
- Codigos HTTP posibles
  `201 Created`
  `400 Bad Request`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos`.

### PUT /catalogo/api/productos/{id}
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos/1`
- Body de ejemplo
```json
{
  "idCategoria": 1,
  "nombre": "Laptop Lenovo ThinkPad T14 Gen 2",
  "precio": 1350.0,
  "stock": 45
}
```
- Respuesta esperada
```json
{
  "message": "Producto actualizado exitosamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos/1`.

### PUT /catalogo/api/productos/{id}/stock
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos/1/stock`
- Body de ejemplo para sumar
```json
{
  "cantidad": 10,
  "operacion": "SUMAR"
}
```
- Body de ejemplo para restar
```json
{
  "cantidad": 10,
  "operacion": "RESTAR"
}
```
- Respuesta esperada
```json
{
  "message": "Stock del producto actualizado exitosamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `409 Conflict`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos/1/stock`.

### DELETE /catalogo/api/productos/{id}
- Metodo HTTP
  `DELETE`
- URL completa
  `http://localhost:8080/NexusGateway/catalogo/api/productos/1`
- Body
  No aplica
- Respuesta esperada
  Sin contenido
- Codigos HTTP posibles
  `204 No Content`
  `404 Not Found`
  `409 Conflict`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8082/NexusMS/api/productos/1`.

## Ingresos

### GET /ingresos/api/ingresos
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/ingresos/api/ingresos`
- Body
  No aplica
- Respuesta esperada
```json
[
  {
    "idIngreso": 1,
    "idUsuario": 1,
    "username": "hwongu",
    "fechaIngreso": "2026-03-26T18:30:00",
    "estado": "RECIBIDO"
  }
]
```
- Codigos HTTP posibles
  `200 OK`
  `502 Bad Gateway`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8083/NexusMS/api/ingresos`.

### GET /ingresos/api/ingresos/{id}/detalles
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/ingresos/api/ingresos/1/detalles`
- Body
  No aplica
- Respuesta esperada
```json
[
  {
    "idDetalle": 1,
    "idIngreso": 1,
    "idProducto": 1,
    "nombreProducto": "Laptop Lenovo ThinkPad T14",
    "cantidad": 10,
    "precioCompra": 950.0
  }
]
```
- Codigos HTTP posibles
  `200 OK`
  `404 Not Found`
  `502 Bad Gateway`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8083/NexusMS/api/ingresos/1/detalles`.

### POST /ingresos/api/ingresos
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8080/NexusGateway/ingresos/api/ingresos`
- Body de ejemplo
```json
{
  "ingreso": {
    "idUsuario": 1,
    "fechaIngreso": "2026-03-26T18:30:00"
  },
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 10,
      "precioCompra": 950.0
    }
  ]
}
```
- Respuesta esperada
```json
{
  "idIngreso": 1,
  "idUsuario": 1,
  "username": "hwongu",
  "fechaIngreso": "2026-03-26T18:30:00",
  "estado": "RECIBIDO"
}
```
- Codigos HTTP posibles
  `201 Created`
  `400 Bad Request`
  `502 Bad Gateway`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8083/NexusMS/api/ingresos`.

### PUT /ingresos/api/ingresos/{id}/anular
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/ingresos/api/ingresos/1/anular`
- Body
  No aplica
- Respuesta esperada
```json
{
  "message": "Ingreso anulado exitosamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `502 Bad Gateway`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8083/NexusMS/api/ingresos/1/anular`.

### PUT /ingresos/api/ingresos/{id}/estado
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8080/NexusGateway/ingresos/api/ingresos/1/estado`
- Body de ejemplo
```json
{
  "estado": "PENDIENTE"
}
```
- Respuesta esperada
```json
{
  "message": "Estado del ingreso actualizado correctamente"
}
```
- Codigos HTTP posibles
  `200 OK`
  `400 Bad Request`
  `404 Not Found`
  `500 Internal Server Error`
- Observaciones
  El gateway reenvia esta peticion a `http://localhost:8083/NexusMS/api/ingresos/1/estado`.

## Actuator

### GET /actuator/health
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/actuator/health`
- Body
  No aplica
- Respuesta esperada
```json
{
  "status": "UP"
}
```
- Codigos HTTP posibles
  `200 OK`
  `500 Internal Server Error`

### GET /actuator/info
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8080/NexusGateway/actuator/info`
- Body
  No aplica
- Respuesta esperada
```json
{}
```
- Codigos HTTP posibles
  `200 OK`
  `500 Internal Server Error`

## Observacion general

Las rutas del gateway usan `StripPrefix=2` y `PrefixPath=/NexusMS`. Eso significa que:

- `/NexusGateway/seguridad/api/...` se reenvia como `/NexusMS/api/...` a `NexusSeguridadMs`
- `/NexusGateway/catalogo/api/...` se reenvia como `/NexusMS/api/...` a `NexusCatalogoMs`
- `/NexusGateway/ingresos/api/...` se reenvia como `/NexusMS/api/...` a `NexusIngresoMs`