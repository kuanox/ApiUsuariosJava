# API de Gestión de Usuarios

## 🏠 Descripción

Una API RESTful para la gestión de usuarios con autenticación JWT, construida con Spring Boot. Permite operaciones CRUD completas sobre entidades de usuarios, incluyendo información de teléfono en formato JSON. Incluye documentación interactiva con Swagger UI, validación de datos, manejo robusto de errores y pruebas unitarias e integradas.

## ✨ Características

- 🔐 **Autenticación JWT**: Sistema seguro de autenticación con tokens JWT válidos por 12 horas
- 👥 **Gestión de Usuarios**: CRUD completo para entidades de usuarios
- 📞 **Teléfonos JSON**: Soporte para múltiples teléfonos con código de ciudad y país
- 📄 **Documentación Swagger**: UI interactiva para explorar y probar la API
- ✅ **Validación de Datos**: Validación automática con Jakarta Validation
- 🛡️ **Seguridad**: Configuración de Spring Security con filtros JWT
- 🔒 **Encriptación BCrypt**: Contraseñas encriptadas de forma segura
- 🧪 **Pruebas Completas**: Tests unitarios, con Mock y pruebas de integración
- 🗄️ **Base de Datos**: H2 para desarrollo (en memoria)
- 🌐 **Mensajes en Español**: Todas las respuestas y errores en español

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Descripción |
|------------|---------|-------------|
| **Spring Boot** | 3.5.11 | Framework principal para el backend |
| **Java** | 21 | Lenguaje de programación |
| **H2 Database** | - | Base de datos embebida para desarrollo |
| **JWT (JJWT)** | - | Biblioteca para tokens JWT |
| **Lombok** | 1.18.32 | Reducción de código boilerplate |
| **Spring Security** | - | Framework de seguridad |
| **SpringDoc OpenAPI** | 2.6.0 | Generación de documentación Swagger |
| **Jackson** | - | Procesamiento JSON |
| **BCrypt** | - | Encriptación segura de contraseñas |
| **JUnit 5** | - | Framework de pruebas |
| **Mockito** | - | Framework para Mock en pruebas |
| **Maven** | - | Gestión de dependencias y build |

## 🚀 Inicio Rápido

### 📋 Prerrequisitos

- **Java 21** o superior instalado
- **Maven 3.6+** para gestión de dependencias

### 🏃‍♂️ Ejecución del Proyecto

```bash
# Compilar el proyecto
./mvnw clean compile

# Empaquetar
./mvnw package

# Ejecutar la aplicación
./mvnw spring-boot:run
```

O ejecutar directamente el JAR:

```bash
java -jar target/retobci-0.0.1-SNAPSHOT.jar
```

### 🌐 Acceso a la Aplicación

Una vez ejecutada, la aplicación estará disponible en:

- **API Base**: `http://localhost:8080`
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`
- **H2 Console**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:retodb`
  - Usuario: `sa`
  - Contraseña: `password`

## 📚 Uso de la API

### 🔑 Autenticación

Primero, registra un usuario o inicia sesión para obtener un token JWT:

#### Registro de Usuario
```bash
POST /api/v1/auth/register
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "password": "Password123",
  "phones": [
    {
      "number": "987654321",
      "citycode": "56",
      "countrycode": "56"
    }
  ]
}
```

**Respuesta (201 Created):**
```json
{
  "mensaje": "Usuario creado exitosamente",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "data": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "isActive": true,
    "phones": [...],
    "created": "2026-02-26T18:00:00",
    "modified": "2026-02-26T18:00:00",
    "lastLogin": "2026-02-26T18:00:00"
  }
}
```

#### Inicio de Sesión
```bash
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "juan@example.com",
  "password": "Password123"
}
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Login exitoso",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "data": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "isActive": true,
    "phones": [...],
    "created": "2026-02-26T18:00:00",
    "modified": "2026-02-26T18:00:00",
    "lastLogin": "2026-02-26T18:00:00"
  }
}
```

### 👤 Gestión de Usuarios

Incluye el token JWT en el header `Authorization: Bearer <token>` para las siguientes operaciones.

#### Obtener Todos los Usuarios
```bash
GET /api/v1/usuarios
Authorization: Bearer <token>
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Usuarios obtenidos exitosamente",
  "data": [
    {
      "id": 1,
      "name": "Juan Pérez",
      "email": "juan@example.com",
      "isActive": true,
      "phones": [...],
      "created": "2026-02-26T18:00:00",
      "modified": "2026-02-26T18:00:00",
      "lastLogin": "2026-02-26T18:00:00"
    }
  ]
}
```

#### Obtener Usuario por ID
```bash
GET /api/v1/usuarios/1
Authorization: Bearer <token>
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Usuario obtenido exitosamente",
  "data": {
    "id": 1,
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "isActive": true,
    "phones": [...],
    "created": "2026-02-26T18:00:00",
    "modified": "2026-02-26T18:00:00",
    "lastLogin": "2026-02-26T18:00:00"
  }
}
```

#### Actualizar Usuario
```bash
PUT /api/v1/usuarios/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Juan Carlos Pérez",
  "email": "juan.carlos@example.com",
  "password": "NewPassword123",
  "phones": [
    {
      "id": 1,
      "number": "999999999",
      "citycode": "56",
      "countrycode": "56"
    }
  ]
}
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Usuario actualizado exitosamente",
  "data": {
    "id": 1,
    "name": "Juan Carlos Pérez",
    "email": "juan.carlos@example.com",
    "isActive": true,
    "phones": [...],
    "created": "2026-02-26T18:00:00",
    "modified": "2026-02-26T18:35:00",
    "lastLogin": "2026-02-26T18:00:00"
  }
}
```

#### Eliminar Usuario
```bash
DELETE /api/v1/usuarios/1
Authorization: Bearer <token>
```

**Respuesta (200 OK):**
```json
{
  "mensaje": "Usuario eliminado exitosamente"
}
```

## 🗂️ Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/previred/api/usuarios/
│   │   ├── config/               # Configuraciones (Security, JWT, Swagger)
│   │   │   ├── SecurityConfig.java
│   │   │   ├── JwtService.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   ├── CustomUserDetailsService.java
│   │   │   ├── CustomAuthenticationEntryPoint.java
│   │   │   ├── CustomAccessDeniedHandler.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/           # Controladores REST
│   │   │   ├── AuthController.java
│   │   │   └── UsuarioController.java
│   │   ├── dto/                 # Objetos de Transferencia de Datos
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegistroUsuarioDTO.java
│   │   │   ├── UsuarioResponseDTO.java
│   │   │   ├── UserRequest.java
│   │   │   ├── TelefonoDTO.java
│   │   │   ├── ResponseMessage.java
│   │   │   ├── AuthResponse.java
│   │   │   └── ErrorMessage.java
│   │   ├── model/               # Entidades JPA
│   │   │   ├── Usuario.java
│   │   │   └── Phone.java
│   │   ├── repository/          # Repositorios de datos
│   │   │   └── UsuarioRepository.java
│   │   ├── service/             # Lógica de negocio
│   │   │   └── UsuarioService.java
│   │   └── RetobciApplication.java
│   └── resources/               # Archivos de configuración
│       └── application.properties
└── test/                        # Pruebas
    ├── UsuarioIntegrationTest.java
    ├── controller/
    │   ├── AuthControllerTest.java
    │   └── UsuarioControllerTest.java
    └── service/
        └── UsuarioServiceTest.java
```

## 🧪 Pruebas

Ejecutar todas las pruebas:

```bash
./mvnw test
```

Ejecutar una prueba específica:

```bash
./mvnw test -Dtest=UsuarioIntegrationTest
```

### Tipos de Pruebas Incluidas

- **Pruebas Unitarias**: Tests de servicios con Mockito
- **Pruebas de Controlador**: Tests REST con MockMvc
- **Pruebas de Integración**: Tests completos del flujo de registro, login y CRUD

## 🔐 Seguridad

### Autenticación JWT
- Tokens válidos por **12 horas**
- Algoritmo: **HS256 (HMAC-SHA256)**
- Incluye en header: `Authorization: Bearer <token>`

### Encriptación de Contraseñas
- Utiliza **BCrypt** para encriptación segura
- Las contraseñas nunca se devuelven en las respuestas

### Endpoints Públicos (Sin token requerido)
- `POST /api/v1/auth/register` - Registrar usuario
- `POST /api/v1/auth/login` - Iniciar sesión

### Endpoints Protegidos (Token requerido)
- `GET /api/v1/usuarios` - Obtener todos los usuarios
- `GET /api/v1/usuarios/{id}` - Obtener usuario por ID
- `PUT /api/v1/usuarios/{id}` - Actualizar usuario
- `DELETE /api/v1/usuarios/{id}` - Eliminar usuario

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `server.port` | Puerto del servidor | 8080 |
| `security.jwt.secret-key` | Clave secreta JWT | (configurada) |
| `security.jwt.expiration-time` | Tiempo de expiración JWT | 43200000 (12h) |
| `user.email.pattern` | Patrón de validación email | (configurado) |
| `user.password.pattern` | Patrón de validación contraseña | (configurado) |

## 📖 Validaciones

### Email
- Formato válido requerido (ej: usuario@dominio.com)
- Debe ser único en la base de datos
- Patrón: `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$`

### Contraseña
- Mínimo 6 caracteres
- Solo letras y números
- Patrón: `^[a-zA-Z0-9]{6,}$`
- Se almacena encriptada con BCrypt

### Teléfono
- Campos: number, citycode, countrycode
- Múltiples teléfonos permitidos

## 📝 Códigos de Respuesta HTTP

| Código | Significado |
|--------|-------------|
| 200 | OK - Operación exitosa |
| 201 | Created - Recurso creado |
| 204 | No Content - Lista vacía |
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - Sin autenticación/token inválido |
| 404 | Not Found - Recurso no encontrado |
| 500 | Server Error - Error del servidor |

## 🌍 Idioma

- ✅ **100% en Español**: Todos los mensajes de error y respuesta están en español
- ✅ **Validaciones claras**: Mensajes específicos para cada tipo de error
- ✅ **Documentación Swagger**: Disponible en swagger-ui.html

## 📝 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles.

## 👨‍💻 Autor

Desarrollado como API REST para gestión de usuarios con autenticación JWT.




