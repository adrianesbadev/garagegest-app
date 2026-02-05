# FRONTEND_GUIDELINES

## Stack de UI

- **Thymeleaf** — Motor de plantillas (servidor).
- **HTML5** — Marcado.
- **CSS** — Un único archivo global: `static/css/app.css`. Variables CSS para tokens (colores, radios, sombras). Sin Tailwind, CSS modules ni styled-components.
- **JavaScript** — Vanilla ES6+. Sin framework frontend; sin Node/npm ni build de assets.

## Estructura de templates

- **Layout:** `templates/layout.html` define el fragment `layout(content)`. El contenido de cada página se inyecta con `th:replace="${content}"`.
- **Páginas con layout:** Cada vista que usa layout declara `th:replace="~{layout :: layout(~{::section})}"` y pone su contenido dentro de `<section>`. Ejemplos: landing, home, clientes (list, form, detail), vehiculos (list, form, detail), ordenes-trabajo (list, form), recordatorios (list, form), usuarios (list, form), error y error/403, 404, 500.
- **Página sin layout:** `login.html` es una página independiente (HTML completo, propio head/body); no usa el fragment de layout para mantener una pantalla de acceso aislada.

## Estilos

- **Archivo:** `src/main/resources/static/css/app.css`.
- **Tokens:** Variables en `:root` (tema claro) y `[data-theme="dark"]` (tema oscuro). Usar siempre estas variables para colores, espaciados y radios; no hardcodear valores que ya existan como variable.
- **Regla:** No añadir archivos CSS nuevos sin criterio; mantener un solo archivo hasta que se justifique un split o un DESIGN_SYSTEM.md que indique lo contrario.

## Scripts

- **En layout (globales):** theme-toggle.js, form-submit.js, notifications.js, validations.js, tooltips.js, table-sort.js. Orden de carga según `layout.html`.
- **Por página:** Incluir solo cuando aplique: list-search.js (listados), ordenes-trabajo.js, recordatorios.js, etc. Ubicación: `static/js/`. Convención de nombres: kebab-case, descriptivo del ámbito (list-search, theme-toggle).
- **Global inline:** La función `changePageSize(newSize)` está definida en un script inline en el layout para paginación.

## Componentes visuales

- Los componentes (botones, cards, tablas, modales, toasts, badges, formularios) están definidos por **clases en app.css**. No hay DESIGN_SYSTEM.md aún; al añadir o modificar UI, reutilizar las clases existentes antes de crear nuevas.

## Accesibilidad y temas

- **Tema claro/oscuro:** Controlado por `data-theme="dark"` en el elemento raíz (o el que aplique). El botón de cambio de tema debe mantener accesibilidad (aria-label, título).
- **Focus y teclado:** Respetar estados de foco visibles (p. ej. `--focus-ring` en el sistema de estilos).
- **ARIA:** Usar atributos ARIA donde mejoren la experiencia con lectores de pantalla (modales, toasts, formularios con error).
