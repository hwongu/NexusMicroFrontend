# 🚀 Proyecto Nexus – Sistema de Gestión de Almacén (Microservicios & Microfrontends)

Este repositorio contiene el código fuente, la configuración de la base de datos y la infraestructura para el sistema **Nexus** en su versión más evolucionada: **Arquitectura de Microservicios con Arquitectura Hexagonal y Microfrontends**. Este proyecto ha sido diseñado como material educativo para clases universitarias, demostrando cómo construir un ecosistema distribuido altamente escalable y desacoplado, tanto en el Backend como en el Frontend.

El sistema implementa:
* **Backend Hexagonal:** Lógica de negocio aislada e independiente de frameworks utilizando el patrón de Puertos y Adaptadores (Arquitectura Hexagonal) en cada microservicio.
* **Microfrontends (MFE):** Interfaz de usuario dividida en sub-aplicaciones independientes (Auth, Dashboard, Categorías, etc.) orquestadas por una aplicación Shell principal.
* **Descubrimiento de Servicios:** Implementación de un servidor **Eureka** para el registro y localización dinámica.
* **Configuración Centralizada:** Implementación de un **Spring Cloud Config Server** para administrar los archivos `.properties`.
* **Tolerancia a Fallos:** Implementación de **Circuit Breakers (Resilience4j)** para evitar fallos en cascada.
* **Enrutamiento Centralizado:** Un API Gateway que actúa como punto de entrada único.

---

## 📁 Estructura del Repositorio

El proyecto está dividido en cinco grandes módulos principales:

### 🗄️ 1_DataBase (Persistencia Efímera)
Contiene la infraestructura de datos dockerizada para el ambiente de desarrollo.
* Aloja un archivo `docker-compose.yml` que levanta tres instancias aisladas de **PostgreSQL 18** (Seguridad, Catálogo e Ingresos) y ejecuta sus scripts SQL. 

### ☕ 2_BackEnd (Lógica de Negocio Hexagonal y Ecosistema Spring)
Contiene los servicios construidos con **Java 21** y **Spring Boot 4.0.5**, implementando **Arquitectura Hexagonal**.
* **NexusEurekaServerMs:** Servidor de descubrimiento de servicios (Service Registry).
* **NexusConfigServerMs:** Servidor de configuración centralizada (`config-repo`).
* **NexusSeguridadMs:** Microservicio de IAM y gestión de usuarios.
* **NexusCatalogoMs:** Microservicio de productos y categorías.
* **NexusIngresoMs:** Microservicio de operaciones transaccionales.
* **NexusGatewayMs:** API Gateway construido con Spring Cloud Gateway.

### 💻 3_FrontEnd (Interfaz de Usuario Distribuida)
Contiene las aplicaciones cliente construidas en **Angular 19**.
* **NexusFrontEnd.MFE:** Directorio principal del ecosistema frontend. Dentro de la carpeta `projects` se encuentran:
  * `NexusFrontEnd`: Aplicación Shell (Contenedor principal).
  * `mfe-auth`: Módulo de inicio de sesión.
  * `mfe-dashboard`: Pantalla de bienvenida.
  * `mfe-categories`: CRUD de categorías.
  * `mfe-products`: CRUD de productos.
  * `mfe-users`: CRUD de usuarios.
  * `mfe-revenues`: Ingreso de productos al almacén.
* **utilitarios_ejecutables:** Carpeta con scripts `.bat` para facilitar el trabajo del desarrollador:
  * `compilar.bat`: Compila el proyecto (ideal para usar en el IDE).
  * `iniciar-todo-desarrollo.bat`: Levanta todos los MFEs en modo local para desarrollo.
  * `generar-instalador-produccion.bat`: Genera los empaquetados finales para el servidor Nginx.
  * `limpiar-proyecto.bat`: Limpia dependencias y archivos generados para subir el código a Git limpiamente.

### 🐳 4_Infraestructure (Despliegue Full-Stack)
Contiene los directorios de destino para levantar la infraestructura completa mediante Docker Compose con **arranque escalonado (Staggered Startup)**.
* **NexusInfraestructura\backend:** Contiene las carpetas para alojar los `.jar` (`deploy_catalogo`, `deploy_seguridad`, etc.) y el `config-repo`.
* **NexusInfraestructura\frontend:** Carpeta de destino donde convivirán todas las carpetas compiladas de los Microfrontends servidas por Nginx.

### 🧪 5_Test (Pruebas de API)
Contiene las colecciones y entornos preconfigurados para validar los endpoints del ecosistema de manera independiente.
* **Postman:** Directorio que aloja los archivos JSON exportados listos para ser importados en tu cliente HTTP.

---

## ⚠️ Reglas de Oro (Para desarrollo y despliegue)

Para garantizar que el entorno funcione correctamente, el orden de ejecución es fundamental:

1.  **La Base de Datos manda:** Los contenedores de bases de datos deben estar encendidos antes de probar los microservicios desde tu IDE.
2.  **Orden Estricto de Inicio (Ejecución en IDE):** Para probar y depurar los microservicios localmente, es **obligatorio** iniciarlos en la siguiente secuencia para evitar errores:
    * 1️⃣ `NexusEurekaServerMs` (Esperar a que levante)
    * 2️⃣ `NexusConfigServerMs` (Provee las configuraciones)
    * 3️⃣ `NexusSeguridadMs`
    * 4️⃣ `NexusCatalogoMs`
    * 5️⃣ `NexusIngresoMs` (Depende de Catálogo y Seguridad)
    * 6️⃣ `NexusGatewayMs` (El último en entrar)
3.  **Aislamiento de Entornos:** No puedes tener levantada la base de datos de la carpeta `1_DataBase` al mismo tiempo que intentas levantar toda la infraestructura desde `4_Infraestructure`.
4.  **Construcción independiente:** Asegúrate de compilar los 6 proyectos del backend y ejecutar el script de producción del frontend antes de intentar el despliegue final.

---

## 🚀 Guía de Ejecución Paso a Paso

### 🗄️ Fase 0: Levantar las Bases de Datos (Ambiente de Desarrollo)
1. Navega a la ruta: `1_DataBase`
2. Levanta las bases de datos ejecutando: `docker-compose up -d`
3. Antes de pasar a la Fase 3, asegúrate de destruir este entorno ejecutando: `docker-compose down -v`

### ☕ Fase 1: Compilar el Ecosistema Backend (Java 21)
1. Navega a la ruta: `2_BackEnd`
2. Entra a la carpeta de cada uno de los **6 proyectos** y genera el empaquetado ejecutando:
   `mvn clean install`
3. Identifica el archivo `.jar` generado dentro de la carpeta `target/` de cada proyecto.

### 💻 Fase 2: Compilar el Frontend (Microfrontends)
1. Navega a la ruta: `3_FrontEnd\NexusFrontEnd.MFE\utilitarios_ejecutables`
2. Ejecuta el script:
   `generar-instalador-produccion.bat`
3. Esto creará una carpeta raíz `dist/`. Navega hacia adentro hasta encontrar la carpeta **`dist/frontend`**. Allí verás todas las subcarpetas de los MFEs listos para producción.

### 🐳 Fase 3: Preparar la Infraestructura Completa (Despliegue Total)
**⚠️ Importante:** La base de datos de la Fase 0 debe estar **detenida y destruida**.

1. **Para los Servicios Base:** Copia los `.jar` obtenidos en la Fase 1 y pégalos en `4_Infraestructure\NexusInfraestructura\backend`:
   * El `.jar` de Eureka en `deploy_eureka`
   * El `.jar` del Config Server en `deploy_config`
2. **Para la Configuración:** Copia la carpeta `config-repo` y pégala directamente dentro de `4_Infraestructure\NexusInfraestructura\backend\config-repo`.
3. **Para los Microservicios:** Pega los `.jar` restantes en `deploy_catalogo`, `deploy_seguridad`, `deploy_ingresos` y `deploy_gateway`.
4. **Para el Frontend (Nginx):** Copia **todo el contenido** de la carpeta `dist/frontend` (obtenida en la Fase 2) y reemplaza el contenido de `4_Infraestructure\NexusInfraestructura\frontend`. Deben quedar exactamente las 7 carpetas (`mfe-auth`, `mfe-categories`, `mfe-dashboard`, `mfe-products`, `mfe-revenues`, `mfe-users`, `NexusFrontEnd`).
5. **Levantar el Sistema:** Navega a la raíz de la infraestructura y ejecuta:
   `docker-compose up -d`

*(Nota: El orquestador cuenta con retardos programados `sleep` para garantizar un arranque escalonado. El sistema completo tardará aproximadamente 45 a 60 segundos en estar 100% operativo).*

---

## 💡 Tip Extra: Levantar Múltiples Instancias Localmente

Si necesitas levantar una instancia extra de un mismo microservicio en tu entorno de desarrollo local (por ejemplo, para probar cómo Eureka hace balanceo de carga), puedes hacerlo fácilmente desde la línea de comandos sin tener que modificar el código fuente ni el IDE.

Navega a la raíz del proyecto que deseas duplicar y ejecuta desde tu CMD:
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8084"

*(Solo cambia el número 8084 por el puerto libre que desees utilizar para esta nueva instancia).*

---

## 🧪 Pruebas del API con Postman

Para facilitar la validación del ecosistema backend de forma aislada, se han incluido scripts de prueba parametrizados. La colección cubre peticiones directas a los microservicios, al Gateway, a Eureka y al Config Server.

1. Navega a la ruta: `5_Test\Postman`.
2. Abre tu herramienta **Postman** (o Insomnia).
3. Utiliza la opción **Import** y selecciona los dos archivos incluidos:
   * `NexusMicrofrontend.postman_collection.json` (Contiene las colecciones de peticiones).
   * `NexusMicrofrontend_Enviroment.postman_environment.json` (Contiene las variables dinámicas).
4. **¡Muy importante!** En la esquina superior derecha de Postman, asegúrate de seleccionar el entorno `NexusMicrofrontend_Enviroment`.
5. Ve a la vista de configuración del entorno e ingresa los valores base en la columna **Current Value** según los puertos que estés utilizando. Deberás llenar:
   * `baseUrlCatalogo`
   * `baseUrlSeguridad`
   * `baseUrlIngresoMs`
   * `baseUrlGateway`
   * `baseUrlEureka`
   * `baseUrlConfigServer`
6. Guarda los cambios del entorno. Ahora todas las peticiones se enrutarán dinámicamente.

---

## 🛠️ Stack Tecnológico

* **Backend:** Java 21, Spring Boot 4.0.5, Spring Data JPA, Lombok
* **Arquitectura de Software:** Arquitectura Hexagonal (Puertos y Adaptadores)
* **Cloud & Patrones:** Spring Cloud Netflix Eureka, Spring Cloud Config, Spring Cloud Gateway, Resilience4j
* **Testing:** JUnit 5, Mockito, Postman (API Testing)
* **Frontend:** Angular 19 (Arquitectura de Microfrontends / Module Federation)
* **Base de Datos:** PostgreSQL 18
* **Infraestructura:** Docker, Docker Compose & Nginx

---

## 👤 Autor
**Autor:** [Henry Wong](https://github.com/hwongu)  

---

## 📜 Licencia

Este proyecto está protegido por copyright © 2026 **Henry Wong**.  
Está permitido su uso únicamente con fines **educativos y académicos** en el marco de cursos universitarios.  
**Queda prohibido su uso en entornos de producción o con fines comerciales.**

---

## ⚠️ Nota

Este repositorio es un recurso de ejemplo para prácticas en clase. No está optimizado para ambientes reales ni cumple con todas las medidas de seguridad y escalabilidad requeridas en aplicaciones comerciales.

---

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Cloud](https://img.shields.io/badge/Spring_Cloud-6DB33F?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-Build-C71A22?style=for-the-badge&logo=apachemaven)
![Angular](https://img.shields.io/badge/Angular-19-DD0031?style=for-the-badge&logo=angular)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-336791?style=for-the-badge&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=for-the-badge&logo=docker)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge&logo=nginx)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)