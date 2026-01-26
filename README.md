# 🚗 GarageGest

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-brightgreen?style=for-the-badge&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?style=for-the-badge)
![License](https://img.shields.io/badge/License-Proprietary-red?style=for-the-badge)

**Sistema de gestión integral para talleres mecánicos**

[Características](#-características) • [Tecnologías](#-tecnologías) • [Instalación](#-instalación) • [Uso](#-uso)

</div>

---

## 📋 Descripción

**GarageGest** es una aplicación web completa desarrollada con **Spring Boot** para la gestión integral de talleres mecánicos y autónomos. Permite gestionar de forma eficiente clientes, vehículos, órdenes de trabajo y recordatorios (ITV, seguros, revisiones), con un sistema robusto de roles y permisos que adapta la interfaz según el tipo de usuario.

Este proyecto forma parte de mi **Trabajo de Fin de Grado (TFG)** del ciclo formativo de **Desarrollo de Aplicaciones Multiplataforma (DAM)**.

---

## ✨ Características

### 🔐 Autenticación y Autorización
- Sistema de login seguro con **Spring Security**
- Tres roles de usuario con permisos diferenciados:
  - **Administrador**: Acceso completo al sistema
  - **Recepción**: Gestión de clientes, vehículos, órdenes y documentos
  - **Mecánico**: Consulta y actualización de órdenes de trabajo asignadas

### 👥 Gestión de Clientes
- CRUD completo de clientes
- Validación de datos (NIF, teléfono, email)
- Historial completo de vehículos y órdenes por cliente
- Exportación de datos a CSV

### 🚙 Gestión de Vehículos
- Registro completo de vehículos con validación de matrículas españolas
- Soporte para matrículas antiguas (1971-2000) y nuevas (2000+)
- Control de kilometraje actual
- Historial de órdenes de trabajo por vehículo

### 🔧 Órdenes de Trabajo
- Creación y seguimiento de órdenes de trabajo
- Estados: Abierta, En Curso, Terminada, Entregada
- Cálculo automático de IVA (21%) y totales
- Asignación a mecánicos
- **Generación de facturas PDF** profesionales con logo de empresa
- Exportación a CSV

### 📅 Recordatorios
- Gestión de recordatorios (ITV, seguros, revisiones)
- Modos: Por fecha, por kilometraje, o ambos
- Validación de kilometraje mínimo basado en el vehículo
- Alertas de recordatorios próximos

### 📊 Dashboard
- Resumen de estadísticas del taller
- Recordatorios próximos a vencer
- Órdenes pendientes asignadas al mecánico logueado
- Resumen de facturación del mes

### 🎨 Interfaz de Usuario
- Diseño moderno y responsive
- **Modo oscuro/claro** con toggle persistente
- Búsqueda y filtrado en tiempo real
- Ordenamiento de columnas en tablas
- Paginación en todas las listas
- Notificaciones toast personalizadas
- Modales de confirmación personalizados
- Breadcrumbs para navegación mejorada
- Estados vacíos con iconos SVG

### 🔍 Funcionalidades Adicionales
- Búsqueda en tiempo real en todas las listas
- Ordenamiento de columnas
- Paginación configurable
- Exportación a CSV (clientes, vehículos, órdenes)
- Validaciones frontend y backend
- Manejo centralizado de errores
- Páginas de error personalizadas (403, 404, 500)

---

## 🛠️ Tecnologías

### Backend
- **Java 17**
- **Spring Boot 3.5.9**
  - Spring Data JPA (Hibernate)
  - Spring Security
  - Spring Validation
- **Maven** (gestión de dependencias)
- **Lombok** (reducción de boilerplate)

### Frontend
- **Thymeleaf** (templating engine)
- **HTML5 / CSS3** (diseño personalizado)
- **JavaScript (ES6+)** (interactividad)
- **Bootstrap** (principios de diseño)
- **SVG Icons** (iconografía)

### Base de Datos
- **MySQL 8.0** (base de datos relacional)

### Herramientas
- **Apache PDFBox 3.0.6** (generación de PDFs)
- **Spring DevTools** (desarrollo)
- **DBeaver** (gestión de base de datos)

---

## 📦 Instalación

### Requisitos Previos
- **Java 17** o superior
- **Maven 3.6+** (o usar Maven Wrapper incluido)
- **MySQL 8.0** (local o remoto)
- **Git** (para clonar el repositorio)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/adrianesbadev/garagegest-app.git
   cd garagegest-app
   ```

2. **Configurar la base de datos MySQL**
   ```sql
   CREATE DATABASE taller_db;
   CREATE USER 'taller_user'@'localhost' IDENTIFIED BY 'TU_CONTRASEÑA';
   GRANT ALL PRIVILEGES ON taller_db.* TO 'taller_user'@'localhost';
   FLUSH PRIVILEGES;
   ```

3. **Ejecutar el script SQL**
   ```sql
   -- Ejecutar el script de creación de tablas
   -- (Ver sección de Estructura de Base de Datos)
   ```

4. **Configurar la aplicación**
   - Copiar `src/main/resources/application.yaml.example` a `application.yaml`
   - Editar `application.yaml` con tus credenciales de MySQL:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://127.0.0.1:3306/taller_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
       username: taller_user
       password: TU_CONTRASEÑA
   ```

5. **Ejecutar la aplicación**
   ```bash
   ./mvnw spring-boot:run
   ```
   
   O si tienes Maven instalado:
   ```bash
   mvn spring-boot:run
   ```

6. **Acceder a la aplicación**
   - Abre tu navegador en: `http://localhost:8080`
   - La aplicación redirigirá al login

### Crear Usuario Administrador

Puedes crear un usuario administrador directamente en la base de datos:

```sql
INSERT INTO USUARIO (username, password_hash, nombre, email, rol, activo, fecha_alta)
VALUES ('admin', '$2a$10$TU_HASH_BCRYPT_AQUI', 'Administrador', 'admin@taller.com', 'admin', TRUE, NOW());
```

> **Nota**: Genera el hash BCrypt de tu contraseña usando un generador online o el PasswordEncoder de Spring.

---

## 🗄️ Estructura de Base de Datos

### Tablas Principales

- **USUARIO**: Usuarios del sistema (admin, recepcion, mecanico)
- **CLIENTE**: Información de clientes
- **VEHICULO**: Vehículos asociados a clientes
- **ORDEN_TRABAJO**: Órdenes de trabajo con estados y facturación
- **RECORDATORIO**: Recordatorios de ITV, seguros, revisiones

### Script de Creación

El esquema completo de la base de datos se encuentra en el archivo SQL del proyecto. Las tablas incluyen:
- Claves primarias autoincrementales
- Claves foráneas con integridad referencial
- Índices para optimización
- Valores por defecto y restricciones

---

## 🚀 Uso

### Roles y Permisos

#### 👨‍💼 Administrador
- Acceso completo al sistema
- Gestión de usuarios
- Todas las funcionalidades de recepción y mecánico

#### 👩‍💼 Recepción
- Gestión de clientes y vehículos
- Creación y edición de órdenes de trabajo
- Generación de facturas PDF
- Exportación de datos
- Gestión de recordatorios

#### 🔧 Mecánico
- Consulta de órdenes de trabajo
- Actualización de estado de órdenes asignadas
- Visualización de recordatorios
- **No puede**: eliminar órdenes, generar facturas, exportar datos

### Flujo de Trabajo Típico

1. **Recepción** crea un cliente y registra su vehículo
2. **Recepción** crea una orden de trabajo y la asigna a un **Mecánico**
3. **Mecánico** actualiza el estado de la orden (En Curso → Terminada)
4. **Recepción** marca la orden como Entregada y genera la factura PDF
5. El sistema registra automáticamente la facturación

---

## 📁 Estructura del Proyecto

```
garagegest-app/
├── src/
│   ├── main/
│   │   ├── java/com/adrian/taller_app/
│   │   │   ├── config/          # Configuración (Security, Validation)
│   │   │   ├── controller/      # Controladores MVC
│   │   │   ├── domain/          # Entidades JPA
│   │   │   ├── repository/     # Repositorios Spring Data JPA
│   │   │   ├── service/        # Lógica de negocio
│   │   │   ├── security/       # Servicios de seguridad
│   │   │   ├── validation/     # Validadores personalizados
│   │   │   └── web/            # DTOs y formularios
│   │   └── resources/
│   │       ├── static/         # CSS, JS, imágenes
│   │       ├── templates/      # Plantillas Thymeleaf
│   │       └── application.yaml.example
│   └── test/                   # Tests unitarios
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🎯 Características Técnicas Destacadas

- ✅ **Arquitectura MVC** bien estructurada
- ✅ **Separación de responsabilidades** (Controller → Service → Repository)
- ✅ **Validaciones personalizadas** (NIF, matrículas españolas, teléfonos)
- ✅ **Manejo centralizado de excepciones** con `@ControllerAdvice`
- ✅ **Paginación** con Spring Data JPA
- ✅ **Búsqueda y filtrado** en tiempo real
- ✅ **Generación de PDFs** profesionales
- ✅ **Exportación a CSV** con formato correcto
- ✅ **Seguridad robusta** con Spring Security
- ✅ **Interfaz responsive** y moderna
- ✅ **Modo oscuro** con persistencia
- ✅ **Optimización de consultas** con `@EntityGraph`

---

## 📸 Capturas de Pantalla

### 🏠 Landing Page
- Muestra el diseño general de la aplicación
- Hero section con las tarjetas de características
- Sección "¿Qué puedes hacer en cada apartado?"

![Landing Page](docs/images/landing.png)

---

### 🔐 Login
- Formulario de login con el logo
- Botón de mostrar/ocultar contraseña
- Diseño moderno y limpio

![Login](docs/images/login.png)

---

### 📊 Dashboard / Resumen
- Estadísticas del taller (clientes, vehículos, órdenes abiertas)
- Resumen de facturación (total facturado, ticket medio, pendiente)
- Recordatorios próximos
- Órdenes pendientes asignadas (si eres mecánico)

![Dashboard](docs/images/dashboard.png)

---

### 👥 Gestión de Clientes

**1. Lista de Clientes**
- Tabla con datos de clientes
- Barra de búsqueda en tiempo real
- Paginación visible
- Botones de acción (Nuevo, Exportar)

![Lista de Clientes](docs/images/clientes-lista.png)

**2. Formulario de Cliente**
- Campos con validaciones
- Diseño del formulario moderno
- Botones de acción

![Formulario de Cliente](docs/images/clientes-form.png)

**3. Detalle de Cliente**
- Información del cliente
- Pestañas con vehículos asociados
- Lista de órdenes de trabajo del cliente
- Botón "Nuevo vehículo"

![Detalle de Cliente 1](docs/images/clientes-detail-1.png)
![Detalle de Cliente 2](docs/images/clientes-detail-2.png)
![Detalle de Cliente 3](docs/images/clientes-detail-3.png)

---

### 🚙 Gestión de Vehículos

**1. Lista de Vehículos**
- Tabla con vehículos y sus propietarios
- Búsqueda y paginación
- Botones de acción

![Lista de Vehículos](docs/images/vehiculos-lista.png)

**2. Formulario de Vehículo**
- Campos con validación de matrícula
- Selector de cliente
- Validaciones visibles

![Formulario de Vehículo](docs/images/vehiculos-form.png)

**3. Detalle de Vehículo**
- Información del vehículo y propietario
- Pestaña con órdenes de trabajo asociadas
- Botón "Nueva OT"

![Detalle de Vehículo 1](docs/images/vehiculos-detail-1.png)
![Detalle de Vehículo 2](docs/images/vehiculos-detail-2.png)

---

### 🔧 Órdenes de Trabajo

**1. Lista de Órdenes**
- Tabla con órdenes y sus estados (badges de color)
- Filtros por estado
- Búsqueda y paginación
- Botones de acción (Nuevo, Exportar)

![Lista de Órdenes](docs/images/ordenes-lista.png)

**2. Formulario de Orden de Trabajo**
- Campos con cálculo automático de IVA y total
- Selector de vehículo
- Selector de mecánico asignado
- Campos de precio con símbolo €

![Formulario de Orden](docs/images/ordenes-form.png)

**3. Factura PDF Generada**
- Logo de la empresa
- Número de orden
- Datos del cliente y vehículo
- Descripción del trabajo
- Desglose de precios (subtotal, IVA, total)
- Formato profesional

![Factura PDF](docs/images/factura-pdf.png)

---

### 📅 Recordatorios

**1. Lista de Recordatorios**
- Tabla con recordatorios
- Tipos y modos visibles
- Estados y fechas objetivo
- Búsqueda y paginación

![Lista de Recordatorios](docs/images/recordatorios-lista.png)

**2. Formulario de Recordatorio**
- Selector de vehículo
- Tipo de recordatorio (ITV, Seguro, Revisión)
- Modo (Por fecha, Por km, Ambos)
- Campos dinámicos según el modo seleccionado
- Validación de kilometraje mínimo

![Formulario de Recordatorio](docs/images/recordatorios-form.png)

---

### 👤 Gestión de Usuarios (Solo Admin)

**1. Lista de Usuarios**
- Tabla con usuarios y sus roles
- Roles visibles con badges
- Búsqueda y paginación

![Lista de Usuarios](docs/images/usuarios-lista.png)

---

### 🌓 Modo Oscuro

**1. Dashboard en Modo Oscuro**
- Muestra el toggle de modo oscuro (botón sol/luna)
- Interfaz con tema oscuro aplicado
- Buen contraste y legibilidad

![Dashboard Modo Oscuro](docs/images/dashboard-dark.png)

**2. Comparación Modo Claro vs Oscuro**
- Muestra la adaptación del tema
- Toggle visible

| Modo Claro | Modo Oscuro |
|:----------:|:-----------:|
| ![Claro](docs/images/comparison-light.png) | ![Oscuro](docs/images/comparison-dark.png) |

---

### 🎨 Características de UI/UX

**1. Búsqueda en Tiempo Real**
- Barra de búsqueda con texto
- Resultados filtrados en tiempo real

![Busqueda en Tiempo Real](docs/images/busqueda-tiempo-real.png)

**2. Paginación**
- Controles de paginación visibles
- Selector de tamaño de página
- Contador "Mostrando X - Y de Z registros"

![Paginacion](docs/images/paginacion.png)

**3. Notificaciones Toast**
- Mensaje de confirmación personalizado
- Diseño moderno

![Notificacion Toast](docs/images/notificacion-toast.png)

**4. Modal de Confirmación**
- Diseño personalizado (no el alert nativo)
- Botones de acción

![Modal de Confirmacion](docs/images/modal-confirmacion.png)

---

## 🔒 Seguridad

- Contraseñas encriptadas con **BCrypt**
- Protección CSRF habilitada
- Rutas protegidas por roles
- Validación de entrada en frontend y backend
- Archivo `application.yaml` excluido del repositorio (`.gitignore`)

---

## 📝 Estado del Proyecto

✅ **Completado** - Proyecto funcional y listo para producción local

### Funcionalidades Implementadas
- [x] CRUD completo de todas las entidades
- [x] Sistema de autenticación y autorización
- [x] Generación de facturas PDF
- [x] Exportación a CSV
- [x] Dashboard con estadísticas
- [x] Búsqueda y filtrado
- [x] Paginación
- [x] Modo oscuro
- [x] Validaciones personalizadas
- [x] Manejo de errores

### Posibles Mejoras Futuras
- [ ] Despliegue en la nube (Railway, Render, AWS)
- [ ] API REST para integraciones
- [ ] Notificaciones por email
- [ ] Panel de administración para personalizar logo/nombre de empresa
- [ ] Reportes avanzados y gráficos
- [ ] App móvil complementaria

---

## 👨‍💻 Autor

**Adrián Esquivel**

- GitHub: [@adrianesbadev](https://github.com/adrianesbadev)
- LinkedIn: [Adrián Esquivel Barrera](https://www.linkedin.com/in/adrianesbadev/)
- Email: adrianesba@gmail.com

---

## 📄 Licencia

Este proyecto es **propietario** y forma parte de un **Trabajo de Fin de Grado (TFG)** del ciclo formativo de Desarrollo de Aplicaciones Multiplataforma (DAM).

**Uso no comercial permitido**: Este proyecto puede ser visualizado y utilizado con fines educativos, de aprendizaje y portfolio.

**Uso comercial restringido**: El uso comercial, distribución, modificación o venta de este software está estrictamente prohibido sin autorización explícita del autor.

Para consultas sobre licencias comerciales, contacta con:
- Email: adrianesba@gmail.com
- LinkedIn: [Adrián Esquivel Barrera](https://www.linkedin.com/in/adrianesbadev/)

Ver el archivo [LICENSE](LICENSE) para más detalles.

---

## 🙏 Agradecimientos

- Profesores del ciclo formativo DAM
- Compañeros y amigos de clase del ciclo formativo DAM
- Comunidad de Spring Boot
- Documentación oficial de las tecnologías utilizadas

---

<div align="center">

**Desarrollado con ❤️ para la gestión eficiente de talleres mecánicos**

⭐ Si te gusta este proyecto, ¡dale una estrella!

</div>
