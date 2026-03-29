# GUIA DE PRUEBAS POSTMAN

## Microservicio
`NexusSeguridadMs`

## URL base
`http://localhost:8081/NexusMS`

## Usuarios

### GET /api/usuarios
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios`
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
  La respuesta no expone el campo `password`.

### GET /api/usuarios/{id}
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios/1`
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
  Si el id no existe, el mensaje de error es `Usuario no encontrado.`

### POST /api/usuarios
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios`
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
  `username`, `password` y `estado` son obligatorios. La respuesta no expone el campo `password`.

### PUT /api/usuarios/{id}
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios/1`
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
  Si el usuario no existe, el mensaje de error es `Usuario no encontrado.`

### DELETE /api/usuarios/{id}
- Metodo HTTP
  `DELETE`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios/1`
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
  Si el usuario tiene ingresos registrados, el mensaje de error es `No se puede eliminar el usuario porque tiene ingresos registrados a su nombre.`

## Login

### POST /api/usuarios/login
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8081/NexusMS/api/usuarios/login`
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
  Solo autentica usuarios activos. Si las credenciales no son validas, el mensaje de error es `Credenciales invalidas.` La respuesta no expone el campo `password`.

## Formato basico de error

Cuando ocurre un error controlado, la API responde con esta estructura:

```json
{
  "timestamp": "2026-03-26T18:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": {
    "username": "El username es obligatorio."
  },
  "path": "/NexusMS/api/usuarios"
}
```

Si el error no es de validacion, `message` llega como texto.
