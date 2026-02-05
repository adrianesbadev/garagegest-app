# PRD — Product Requirements Document (resumen)

Documento breve de alcance del producto. No sustituye un PRD completo si el proyecto crece.

## Qué es

**GarageGest** — Aplicación web para la gestión integral de talleres mecánicos: clientes, vehículos, órdenes de trabajo y recordatorios (ITV, seguros, revisiones).

## Para quién

- Talleres mecánicos y autónomos.
- Usuarios con roles: **Administrador**, **Recepción**, **Mecánico** (permisos diferenciados).

## Scope (MVP actual)

- Autenticación y autorización por roles.
- CRUD de clientes, vehículos, órdenes de trabajo, recordatorios.
- CRUD de usuarios (solo rol Admin).
- Dashboard (resumen, estadísticas, recordatorios próximos, órdenes pendientes según rol).
- Generación de facturas en PDF.
- Exportación a CSV (clientes, vehículos, órdenes).
- Búsqueda en tiempo real, paginación y ordenación en listados.
- Modo claro/oscuro (persistente).
- Validaciones en frontend y backend; manejo de errores y páginas 403, 404, 500.

## Out of scope

Según README del proyecto, fuera del alcance actual:

- Despliegue en la nube (Railway, Render, AWS, etc.).
- API REST para integraciones.
- Notificaciones por email.
- Panel de administración para personalizar logo/nombre de empresa.
- Reportes avanzados y gráficos.
- App móvil complementaria.
