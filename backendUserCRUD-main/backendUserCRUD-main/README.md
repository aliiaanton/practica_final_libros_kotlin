# Backend CRUD de Usuarios con Spring Boot y JWT

Este proyecto es un backend desarrollado en Spring Boot que implementa un CRUD de usuarios con autenticación y autorización mediante JSON Web Tokens (JWT).

## Características

- **Gestión de usuarios**: Creación, lectura, actualización y eliminación de usuarios.
- **Autenticación**: Proceso de login que genera un JWT para el cliente.
- **Autorización**: Endpoints protegidos que requieren un rol de administrador.
- **Base de datos en memoria**: Uso de H2 para facilitar el desarrollo y las pruebas.
- **Mapeo de objetos**: MapStruct para convertir DTOs a entidades y viceversa.

## Requisitos de Instalación

- **Java 21**: Asegúrate de tener el JDK 21 instalado.
- **Maven**: El proyecto utiliza Maven para la gestión de dependencias.

## Cómo Arrancar el Proyecto

1.  **Clona el repositorio**:
    ```bash
    git clone <URL_DEL_REPOSITORIO>
    ```

2.  **Navega al directorio del proyecto**:
    ```bash
    cd backendUserCRUD
    ```

3.  **Compila y empaqueta el proyecto con Maven**:
    ```bash
    mvn clean install
    ```

4.  **Ejecuta la aplicación**:
    ```bash
    mvn spring-boot:run
    ```

La aplicación se iniciará en el puerto `8080` por defecto.

## Endpoints de la API

A continuación, se detallan los endpoints disponibles en la API.

### Autenticación

-   **`POST /api/auth/register`**: Registra un nuevo usuario.
    -   **Body (raw JSON)**:
        ```json
        {
          "username": "nuevo_usuario",
          "password": "tu_contraseña",
          "firstname": "Nombre",
          "lastname": "Apellido"
        }
        ```

-   **`POST /api/auth/login`**: Inicia sesión y obtiene un token JWT.
    -   **Body (raw JSON)**:
        ```json
        {
          "username": "tu_usuario",
          "password": "tu_contraseña"
        }
        ```
    -   **Respuesta exitosa**:
        ```json
        {
          "token": "el_token_jwt"
        }
        ```

### Administración de Usuarios (Requiere rol de Administrador)

Para acceder a estos endpoints, es necesario incluir el token JWT en la cabecera `Authorization` de la petición:

`Authorization: Bearer <TU_TOKEN_JWT>`

-   **`GET /api/admin/users`**: Obtiene una lista de todos los usuarios.

-   **`PUT /api/admin/users/{id}`**: Actualiza los datos de un usuario existente.
    -   **Body (raw JSON)**:
        ```json
        {
          "firstname": "NuevoNombre",
          "lastname": "NuevoApellido",
          "role": "ADMIN"
        }
        ```
    -   **Nota**: El campo `role` puede ser `ADMIN` o `USER`.

-   **`DELETE /api/admin/users/{id}`**: Elimina un usuario por su ID.

## Base de Datos

El proyecto utiliza una base de datos en memoria H2. Puedes acceder a la consola de H2 a través de la siguiente URL:

[http://localhost:8080/h2-console](http://localhost:8080/h2-console)

**Configuración de la conexión:**

-   **Driver Class**: `org.h2.Driver`
-   **JDBC URL**: `jdbc:h2:mem:testdb`
-   **User Name**: `sa`
-   **Password**: (dejar en blanco)
