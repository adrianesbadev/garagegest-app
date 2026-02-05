# 📋 INFORME COMPLETO DEL PROYECTO GARAGEGEST

## 🎯 Descripción General
**GarageGest** es una aplicación web para la gestión integral de talleres mecánicos desarrollada con Spring Boot 3.5.9 y Java 17. Permite gestionar clientes, vehículos, órdenes de trabajo y recordatorios (ITV, seguros, revisiones).

---

## 📁 ESTRUCTURA DEL PROYECTO

### 📂 Raíz del Proyecto

#### Archivos de Configuración
- **`pom.xml`**: Archivo Maven que define las dependencias del proyecto (Spring Boot, JPA, Security, Thymeleaf, PDFBox, etc.)
- **`.gitignore`**: Define qué archivos/carpetas no deben subirse a Git (target/, .idea/, etc.)
- **`.gitattributes`**: Configuración de Git para normalización de archivos
- **`mvnw`** / **`mvnw.cmd`**: Maven Wrapper (permite ejecutar Maven sin instalarlo)

#### Carpetas
- **`.mvn/wrapper/`**: Contiene el Maven Wrapper y sus propiedades
- **`src/`**: Código fuente del proyecto
- **`target/`**: Archivos compilados (generado automáticamente, no se versiona)

---

## 📂 `src/main/java/com/adrian/taller_app/`

### 🚀 **TallerAppApplication.java**
- **Propósito**: Clase principal de Spring Boot que inicia la aplicación
- **Función**: Punto de entrada de la aplicación, contiene el método `main()`

---

### 📂 `config/` - Configuración de la Aplicación

#### **BootstrapAdminRunner.java**
- **Propósito**: Inicializa datos por defecto al arrancar la aplicación
- **Función**: Crea un usuario administrador si no existe ninguno en la base de datos

#### **SecurityConfig.java**
- **Propósito**: Configuración de Spring Security
- **Función**: 
  - Define las rutas públicas y protegidas
  - Configura roles y permisos (ADMIN, RECEPCION, MECANICO)
  - Configura el sistema de login/logout
  - Define el PasswordEncoder (BCrypt)

#### **ValidationConfig.java**
- **Propósito**: Configuración de validaciones
- **Función**: Configura el LocalValidatorFactoryBean para validaciones personalizadas

---

### 📂 `controller/` - Controladores (MVC)

Los controladores manejan las peticiones HTTP y coordinan entre la vista y el servicio.

#### **ClienteController.java**
- **Ruta base**: `/clientes`
- **Funciones**: 
  - Listar clientes (`GET /clientes`)
  - Crear cliente (`GET/POST /clientes/nuevo`)
  - Editar cliente (`GET/POST /clientes/{id}/editar`)
  - Ver detalle (`GET /clientes/{id}`)
  - Eliminar cliente (`POST /clientes/{id}/eliminar`)
  - Exportar a CSV (`GET /clientes/exportar`)

#### **VehiculoController.java**
- **Ruta base**: `/vehiculos`
- **Funciones**: 
  - Listar vehículos (`GET /vehiculos`)
  - Crear vehículo (`GET/POST /vehiculos/nuevo`)
  - Editar vehículo (`GET/POST /vehiculos/{id}/editar`)
  - Ver detalle (`GET /vehiculos/{id}`)
  - Eliminar vehículo (`POST /vehiculos/{id}/eliminar`)
  - Exportar a CSV (`GET /vehiculos/exportar`)

#### **OrdenTrabajoController.java**
- **Ruta base**: `/ordenes-trabajo`
- **Funciones**: 
  - Listar órdenes (`GET /ordenes-trabajo`)
  - Filtrar por estado
  - Crear orden (`GET/POST /ordenes-trabajo/nueva`)
  - Editar orden (`GET/POST /ordenes-trabajo/{id}/editar`)
  - Eliminar orden (`POST /ordenes-trabajo/{id}/eliminar`)
  - Generar factura PDF (`GET /ordenes-trabajo/{id}/factura`)
  - Exportar a CSV (`GET /ordenes-trabajo/exportar`)

#### **RecordatorioController.java**
- **Ruta base**: `/recordatorios`
- **Funciones**: 
  - Listar recordatorios (`GET /recordatorios`)
  - Crear recordatorio (`GET/POST /recordatorios/nuevo`)
  - Editar recordatorio (`GET/POST /recordatorios/{id}/editar`)
  - Eliminar recordatorio (`POST /recordatorios/{id}/eliminar`)

#### **UsuarioController.java**
- **Ruta base**: `/usuarios`
- **Funciones**: 
  - Listar usuarios (`GET /usuarios`) - Solo ADMIN
  - Crear usuario (`GET/POST /usuarios/nuevo`)
  - Editar usuario (`GET/POST /usuarios/{id}/editar`)
  - Eliminar usuario (`POST /usuarios/{id}/eliminar`)

#### **HomeController.java**
- **Rutas**: `/` y `/resumen`
- **Funciones**: 
  - Página de inicio/landing (`GET /`)
  - Dashboard/Resumen (`GET /resumen`) con estadísticas según rol

#### **LoginController.java**
- **Ruta**: `/login`
- **Funciones**: 
  - Muestra el formulario de login
  - Maneja la autenticación (Spring Security)

#### **GlobalExceptionHandler.java**
- **Propósito**: Manejo centralizado de excepciones
- **Función**: 
  - Captura excepciones de toda la aplicación
  - Devuelve páginas de error apropiadas (404, 403, 500, etc.)
  - Maneja EntityNotFoundException, IllegalStateException, etc.

---

### 📂 `domain/` - Entidades del Dominio

#### **Cliente.java**
- **Propósito**: Entidad JPA que representa un cliente
- **Campos**: idCliente, nombre, telefono, email, nif, fechaAlta
- **Relaciones**: OneToMany con Vehiculo

#### **Vehiculo.java**
- **Propósito**: Entidad JPA que representa un vehículo
- **Campos**: idVehiculo, matricula, marca, modelo, anio, kmActual
- **Relaciones**: ManyToOne con Cliente, OneToMany con OrdenTrabajo y Recordatorio

#### **OrdenTrabajo.java**
- **Propósito**: Entidad JPA que representa una orden de trabajo
- **Campos**: idOt, fechaCreacion, fechaCierre, kmEntrada, descripcion, subtotal, ivaTotal, total, estado
- **Relaciones**: ManyToOne con Vehiculo y Usuario (mecánico asignado)

#### **Recordatorio.java**
- **Propósito**: Entidad JPA que representa un recordatorio (ITV, seguro, revisión)
- **Campos**: idRecordatorio, tipo, modo, fechaObjetivo, kmObjetivo, estado, creadoEn
- **Relaciones**: ManyToOne con Vehiculo

#### **Usuario.java**
- **Propósito**: Entidad JPA que representa un usuario del sistema
- **Campos**: idUsuario, username, passwordHash, nombre, email, rol, activo, fechaAlta

#### **EstadoOrdenTrabajo.java**
- **Propósito**: Enum con los estados posibles de una orden (ABIERTA, EN_CURSO, TERMINADA, ENTREGADA)

#### **ModoRecordatorio.java**
- **Propósito**: Enum con los modos de recordatorio (POR_FECHA, POR_KM, AMBOS)

#### **RolUsuario.java**
- **Propósito**: Enum con los roles del sistema (ADMIN, RECEPCION, MECANICO)

#### 📂 `domain/converter/` - Convertidores JPA
- **EstadoOrdenTrabajoConverter.java**: Convierte entre enum y String para la BD
- **ModoRecordatorioConverter.java**: Convierte entre enum y String para la BD
- **RolUsuarioConverter.java**: Convierte entre enum y String para la BD

---

### 📂 `repository/` - Repositorios (Spring Data JPA)

#### **ClienteRepository.java**
- **Interfaz**: Extiende JpaRepository<Cliente, Long>
- **Métodos personalizados**: 
  - `findWithVehiculosByIdCliente()`: Busca cliente con vehículos cargados
  - `existsByNifIgnoreCase()`: Verifica si existe un NIF
  - `findByNifIgnoreCase()`: Busca por NIF

#### **VehiculoRepository.java**
- **Interfaz**: Extiende JpaRepository<Vehiculo, Long>
- **Métodos personalizados**: 
  - `findWithClienteByIdVehiculo()`: Busca vehículo con cliente cargado
  - `findAllByOrderByMatriculaAsc()`: Lista ordenada por matrícula
  - `existsByMatriculaIgnoreCase()`: Verifica si existe una matrícula
  - `findByMatriculaIgnoreCase()`: Busca por matrícula

#### **OrdenTrabajoRepository.java**
- **Interfaz**: Extiende JpaRepository<OrdenTrabajo, Long>
- **Métodos personalizados**: 
  - `findByIdOt()`: Busca por ID de OT
  - `countByFechaCierreIsNull()`: Cuenta órdenes abiertas
  - `contarOrdenesPorMes()`: Estadísticas mensuales
  - `sumarIngresosPorMes()`: Ingresos mensuales
  - `sumarTotalFacturadoEnPeriodo()`: Total facturado
  - `sumarTotalPendiente()`: Total pendiente de facturar
  - `contarFacturasEnPeriodo()`: Número de facturas

#### **RecordatorioRepository.java**
- **Interfaz**: Extiende JpaRepository<Recordatorio, Long>
- **Métodos personalizados**: 
  - `findProximosAVencer()`: Busca recordatorios próximos a vencer

#### **UsuarioRepository.java**
- **Interfaz**: Extiende JpaRepository<Usuario, Long>
- **Métodos personalizados**: 
  - `findAllByActivoTrueOrderByNombreAsc()`: Lista usuarios activos
  - `findAllByActivoTrueAndRolOrderByNombreAsc()`: Lista por rol
  - `findByUsernameIgnoreCase()`: Busca por username
  - `existsByUsernameIgnoreCase()`: Verifica si existe username

---

### 📂 `service/` - Lógica de Negocio

#### **ClienteService.java**
- **Propósito**: Lógica de negocio para clientes
- **Funciones**: 
  - CRUD completo
  - Validación de NIF único
  - Sanitización de datos
  - Validación de integridad (no eliminar si tiene vehículos)

#### **VehiculoService.java**
- **Propósito**: Lógica de negocio para vehículos
- **Funciones**: 
  - CRUD completo
  - Validación de matrícula única
  - Normalización de matrículas (formato español)
  - Sanitización de datos

#### **OrdenTrabajoService.java**
- **Propósito**: Lógica de negocio para órdenes de trabajo
- **Funciones**: 
  - CRUD completo
  - Cálculo automático de IVA (21%) y total
  - Gestión de estados y fechas de cierre
  - Estadísticas mensuales
  - Resumen de facturación

#### **RecordatorioService.java**
- **Propósito**: Lógica de negocio para recordatorios
- **Funciones**: 
  - CRUD completo
  - Búsqueda de recordatorios próximos a vencer
  - Validación de modos (por fecha, por km, ambos)

#### **UsuarioService.java**
- **Propósito**: Lógica de negocio para usuarios
- **Funciones**: 
  - CRUD completo
  - Validación de username único
  - Encriptación de contraseñas (BCrypt)
  - Gestión de roles y permisos

#### **FacturaPdfService.java**
- **Propósito**: Generación de facturas en PDF
- **Funciones**: 
  - Crea PDFs profesionales con Apache PDFBox
  - Incluye logo, datos del cliente, vehículo y orden
  - Formatea montos con símbolo de euro

#### **CsvExportService.java**
- **Propósito**: Exportación de datos a CSV
- **Funciones**: 
  - Exporta clientes, vehículos y órdenes a CSV
  - Formato compatible con Excel (UTF-8 BOM)
  - Escapado correcto de comas y comillas

---

### 📂 `security/` - Seguridad

#### **UsuarioDetailsService.java**
- **Propósito**: Implementa UserDetailsService de Spring Security
- **Función**: 
  - Carga usuarios desde la BD para autenticación
  - Convierte entidad Usuario a UserDetails de Spring
  - Maneja roles y estado activo/inactivo

---

### 📂 `validation/` - Validaciones Personalizadas

#### **EmailReal.java** / **EmailRealValidator.java**
- **Propósito**: Valida que un email tenga formato válido y dominio real

#### **Nif.java** / **NifValidator.java**
- **Propósito**: Valida formato de NIF/NIE español (DNI/NIE con letra de control)

#### **TelefonoEspanol.java** / **TelefonoEspanolValidator.java**
- **Propósito**: Valida formato de teléfono español (9 dígitos)

#### **MatriculaEspanola.java** / **MatriculaEspanolaValidator.java**
- **Propósito**: Valida matrículas españolas (formato nuevo 2000+ y antiguo 1971-2000)

---

### 📂 `web/` - DTOs (Data Transfer Objects)

#### **UsuarioForm.java**
- **Propósito**: DTO para formularios de usuario
- **Campos**: username, nombre, email, rol, activo, password

#### **ResumenFacturacion.java**
- **Propósito**: DTO para el resumen de facturación del dashboard
- **Campos**: totalFacturado, totalPendiente, numeroFacturas, ticketMedio

#### **EstadisticasMes.java**
- **Propósito**: DTO para estadísticas mensuales de órdenes
- **Campos**: mes, anio, cantidad

#### **IngresosMes.java**
- **Propósito**: DTO para ingresos mensuales
- **Campos**: mes, anio, total

---

## 📂 `src/main/resources/`

### 📄 **application.yaml**
- **Propósito**: Configuración principal de Spring Boot
- **Contenido**: 
  - Configuración de base de datos MySQL
  - Configuración de JPA/Hibernate
  - Puerto del servidor (8080)
  - Nombre de la aplicación

### 📄 **messages.properties**
- **Propósito**: Mensajes de validación y errores
- **Contenido**: Mensajes personalizados para validaciones (NIF, matrícula, email, etc.)

---

### 📂 `static/` - Recursos Estáticos

#### 📂 `css/`
- **app.css**: Hoja de estilos principal (2694 líneas)
  - Variables CSS para tema claro/oscuro
  - Estilos de componentes (botones, formularios, tablas, cards)
  - Estilos responsive
  - Modo oscuro completo
  - Animaciones y transiciones

#### 📂 `images/`
- **logo-garagegest.svg**: Logo vectorial de la aplicación
- **logo-garagegest.png**: Logo en formato PNG

#### 📂 `js/` - JavaScript del Frontend

- **list-search.js**: Sistema de búsqueda en tiempo real para tablas
- **table-sort.js**: Ordenamiento de columnas en tablas
- **notifications.js**: Sistema de notificaciones toast personalizadas
- **validations.js**: Validaciones en tiempo real en formularios
- **theme-toggle.js**: Toggle de modo claro/oscuro
- **tooltips.js**: Gestión de tooltips en botones de acción
- **tabs.js**: Sistema de pestañas para navegación
- **login.js**: Toggle mostrar/ocultar contraseña
- **ordenes-trabajo.js**: Cálculo automático de IVA y total
- **recordatorios.js**: Lógica de campos dinámicos (fecha/km según modo)

---

### 📂 `templates/` - Plantillas Thymeleaf

#### **layout.html**
- **Propósito**: Plantilla base para todas las páginas
- **Contenido**: 
  - Header con navegación y logo
  - Footer
  - Breadcrumbs
  - Botón de toggle de tema
  - Inclusión de CSS y JS comunes

#### **landing.html**
- **Propósito**: Página de inicio pública
- **Contenido**: 
  - Presentación de la aplicación
  - Características principales
  - Acceso para usuarios no autenticados

#### **login.html**
- **Propósito**: Página de inicio de sesión
- **Contenido**: 
  - Formulario de login
  - Toggle mostrar/ocultar contraseña
  - Diseño moderno con animaciones

#### **home.html**
- **Propósito**: Dashboard/Resumen (requiere autenticación)
- **Contenido**: 
  - Estadísticas generales
  - Resumen de facturación (admin/recepción)
  - Recordatorios próximos
  - Órdenes pendientes (mecánicos)

#### **error.html**
- **Propósito**: Página de error genérica
- **Uso**: Errores 404, 500, etc.

#### 📂 `error/`
- **403.html**: Página de acceso denegado (mejorada)
- **404.html**: Página de recurso no encontrado
- **500.html**: Página de error interno del servidor

#### 📂 `clientes/`
- **list.html**: Lista de clientes con búsqueda y ordenamiento
- **form.html**: Formulario crear/editar cliente
- **detail.html**: Detalle de cliente con pestañas (vehículos, órdenes)

#### 📂 `vehiculos/`
- **list.html**: Lista de vehículos con búsqueda y ordenamiento
- **form.html**: Formulario crear/editar vehículo
- **detail.html**: Detalle de vehículo con pestañas (órdenes, recordatorios)

#### 📂 `ordenes-trabajo/`
- **list.html**: Lista de órdenes con filtros por estado, búsqueda y ordenamiento
- **form.html**: Formulario crear/editar orden con cálculo automático de IVA

#### 📂 `recordatorios/`
- **list.html**: Lista de recordatorios con búsqueda y ordenamiento
- **form.html**: Formulario crear/editar recordatorio con campos dinámicos

#### 📂 `usuarios/`
- **list.html**: Lista de usuarios (solo ADMIN)
- **form.html**: Formulario crear/editar usuario

---

## 📂 `src/test/java/`

### **TallerAppApplicationTests.java**
- **Propósito**: Tests unitarios básicos de Spring Boot
- **Estado**: Test básico generado automáticamente

---

## 🔧 TECNOLOGÍAS Y DEPENDENCIAS PRINCIPALES

### Backend
- **Spring Boot 3.5.9**: Framework principal
- **Spring Data JPA**: Acceso a datos
- **Spring Security**: Autenticación y autorización
- **Hibernate**: ORM
- **MySQL**: Base de datos
- **Thymeleaf**: Motor de plantillas
- **Apache PDFBox**: Generación de PDFs
- **Lombok**: Reducción de código boilerplate
- **Bean Validation**: Validaciones

### Frontend
- **HTML5 + CSS3**: Estructura y estilos
- **JavaScript (ES6+)**: Interactividad
- **Thymeleaf**: Integración servidor-cliente
- **Google Fonts (Inter)**: Tipografía

---

## 📊 ESTADÍSTICAS DEL PROYECTO

- **Total de archivos Java**: ~40 archivos
- **Total de plantillas HTML**: ~20 archivos
- **Archivos JavaScript**: 10 archivos
- **Archivos CSS**: 1 archivo principal (2694 líneas)
- **Líneas de código estimadas**: ~15,000+ líneas

---

## 🎯 FUNCIONALIDADES PRINCIPALES

1. ✅ **Gestión de Clientes**: CRUD completo con validaciones
2. ✅ **Gestión de Vehículos**: CRUD con validación de matrículas
3. ✅ **Gestión de Órdenes de Trabajo**: CRUD con estados y facturación
4. ✅ **Gestión de Recordatorios**: ITV, seguros, revisiones
5. ✅ **Gestión de Usuarios**: CRUD con roles y permisos
6. ✅ **Autenticación y Autorización**: Spring Security con roles
7. ✅ **Generación de Facturas PDF**: Facturas profesionales
8. ✅ **Exportación CSV**: Exportación de datos
9. ✅ **Dashboard**: Estadísticas y resúmenes
10. ✅ **Modo Oscuro**: Tema claro/oscuro persistente
11. ✅ **Búsqueda en Tiempo Real**: Filtrado de tablas
12. ✅ **Ordenamiento de Columnas**: Click en headers
13. ✅ **Validaciones**: Frontend y backend
14. ✅ **Responsive Design**: Adaptable a móviles

---

## 📝 NOTAS FINALES

- El proyecto sigue las mejores prácticas de Spring Boot
- Arquitectura en capas (Controller → Service → Repository)
- Validaciones tanto en frontend como backend
- Código documentado con JavaDoc y JSDoc
- Diseño moderno y profesional
- Accesibilidad básica implementada (ARIA labels, roles)
- Sin código muerto o archivos innecesarios

---

**Generado el**: $(date)
**Versión del Proyecto**: 0.0.1-SNAPSHOT
**Autor**: Adrian Esquivel
