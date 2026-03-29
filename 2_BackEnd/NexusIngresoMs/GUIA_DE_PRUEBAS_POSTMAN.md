# GUIA DE PRUEBAS POSTMAN

## Microservicio
`NexusIngresoMs`

## URL base
`http://localhost:8083/NexusMS`

## Ingresos

### GET /api/ingresos
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8083/NexusMS/api/ingresos`
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
  El servicio consulta `NexusSeguridadMs` para enriquecer la respuesta con `username`. Si la integracion no responde, la API puede devolver `502 Bad Gateway`.

### GET /api/ingresos/{id}/detalles
- Metodo HTTP
  `GET`
- URL completa
  `http://localhost:8083/NexusMS/api/ingresos/1/detalles`
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
  Si el ingreso no existe, el mensaje de error es `Ingreso no encontrado.` El servicio consulta `NexusCatalogoMs` para enriquecer la respuesta con `nombreProducto`. Si la integracion no responde, la API puede devolver `502 Bad Gateway`.

### POST /api/ingresos
- Metodo HTTP
  `POST`
- URL completa
  `http://localhost:8083/NexusMS/api/ingresos`
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
    },
    {
      "idProducto": 2,
      "cantidad": 5,
      "precioCompra": 120.0
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
  `ingreso` es obligatorio y `detalles` debe tener al menos un elemento. `idUsuario`, `idProducto`, `cantidad` y `precioCompra` tienen validaciones de obligatoriedad y rango. Antes de guardar, el servicio valida usuario contra `NexusSeguridadMs` y productos contra `NexusCatalogoMs`. Si falla la actualizacion remota de stock despues del guardado local, la API responde `502` y el ingreso queda en estado `ERROR_INTEGRACION`.

### PUT /api/ingresos/{id}/anular
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8083/NexusMS/api/ingresos/1/anular`
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
  Solo se puede anular un ingreso en estado `RECIBIDO`. Si ya esta anulado, el mensaje es `El ingreso ya se encuentra anulado.` Si esta en otro estado, el mensaje es `Solo se puede anular un ingreso en estado RECIBIDO.` Si falla la reversa remota de stock, la API responde `502` y el ingreso queda en estado `ERROR_INTEGRACION`.

### PUT /api/ingresos/{id}/estado
- Metodo HTTP
  `PUT`
- URL completa
  `http://localhost:8083/NexusMS/api/ingresos/1/estado`
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
  `estado` es obligatorio. El servicio guarda el valor en mayusculas.

## Formato basico de error

Cuando ocurre un error controlado, la API responde con esta estructura:

```json
{
  "timestamp": "2026-03-26T18:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": {
    "estado": "El estado es obligatorio."
  },
  "path": "/NexusMS/api/ingresos/1/estado"
}
```

Si el error no es de validacion, `message` llega como texto.
