# APP_FLOW — Flujo de la aplicación

Este documento describe las rutas, vistas y flujos de usuario de GarageGest.

## Rutas públicas

| Ruta | Método | Vista | Nota |
|------|--------|-------|------|
| `/` | GET | landing.html | Página de inicio (sin autenticación) |
| `/login` | GET | login.html | Formulario de acceso (página independiente, no usa layout) |
| `/css/**`, `/js/**`, `/images/**` | GET | estáticos | Recursos estáticos |
| `/error`, `/error/**` | GET | error.html, error/403, 404, 500 | Páginas de error |

Tras login correcto, el usuario es redirigido a `/`.

## Rutas autenticadas

Todas las rutas siguientes requieren usuario autenticado, salvo las públicas indicadas arriba.

| Recurso | Ruta base | Roles | Vistas principales |
|---------|-----------|-------|---------------------|
| Dashboard | `/resumen` | ADMIN, RECEPCION, MECANICO | home.html |
| Clientes | `/clientes` | ADMIN, RECEPCION | list, form, detail |
| Vehículos | `/vehiculos` | ADMIN, RECEPCION | list, form, detail |
| Órdenes de trabajo | `/ordenes-trabajo` | ADMIN, RECEPCION, MECANICO | list, form (solo ADMIN/RECEPCION: eliminar, factura PDF, exportar) |
| Recordatorios | `/recordatorios` | ADMIN, RECEPCION | list, form |
| Usuarios | `/usuarios` | ADMIN | list, form |

## Flujos por tipo de usuario

**Recepción (y Admin):** Cliente → Vehículo → Orden de trabajo → Factura PDF. Gestión de recordatorios (ITV, seguros, revisiones). Exportar CSV en clientes, vehículos y órdenes.

**Mecánico:** Ver órdenes de trabajo asignadas; actualizar estado (En curso, Terminada). No puede eliminar órdenes, generar facturas ni exportar.

**Admin:** Todo lo anterior más CRUD de usuarios (listado, alta, edición, baja).

## Páginas de error

- **403** — Acceso denegado (sin permiso para el recurso). Plantilla: `templates/error/403.html`.
- **404** — Recurso no encontrado. Plantilla: `templates/error/404.html`.
- **500** — Error interno del servidor. Plantilla: `templates/error/500.html`.

Todas las páginas de error usan el layout común (`layout :: layout(~{::section})`).
