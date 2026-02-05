# TECH_STACK

Stack real del proyecto (sin inventar dependencias). Versiones según `pom.xml` y parent de Spring Boot.

## Backend

- **Java:** 17
- **Spring Boot:** 3.5.9 (parent)
- **Maven:** Gestión de dependencias y build (Maven Wrapper en proyecto: `mvnw`)

Dependencias principales:

- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-thymeleaf
- spring-boot-starter-validation
- thymeleaf-extras-springsecurity6
- mysql-connector-j (runtime)
- lombok (optional)
- org.apache.pdfbox:pdfbox — 3.0.6
- spring-boot-devtools (runtime, optional)

Test:

- spring-boot-starter-test (scope test)
- spring-security-test (scope test)

Thymeleaf: versión definida por el BOM de Spring Boot 3.5.9 (no fijada explícitamente en pom).

## Frontend

- **Thymeleaf** — Versión del BOM Spring Boot 3.5.9
- **HTML5 / CSS3** — Un único CSS: `src/main/resources/static/css/app.css`
- **JavaScript** — ES6+, vanilla. Archivos en `static/js/`. Sin `package.json`; no hay build de frontend (npm/node no forman parte del proyecto).

## Base de datos

- **MySQL** — 8.x (compatible con el conector definido en pom). El esquema y credenciales se configuran vía `application.yaml`.

## Configuración

- **application.yaml** — No versionado (incluido en `.gitignore`). Ejemplo versionado: `src/main/resources/application.yaml.example`.

## Herramientas

- Maven Wrapper (`mvnw`, `mvnw.cmd`) para ejecutar Maven sin instalación global.

[TODO: Si se fijan versiones exactas de MySQL o Maven en el equipo, indicarlas aquí.]
