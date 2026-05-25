# Spring Boot Planner

Aplicación web de planificación académica desarrollada con Spring Boot + Thymeleaf.

Permite gestionar el estudio y la carga lectiva mediante módulos de asignaturas, tareas, exámenes, horarios y bloques de estudio, con un dashboard central para visualizar el estado general.

**Idiomas:** Español · [English](README.en.md)

## Demo

- **Aplicación en vivo:** [https://spring-boot-planner.onrender.com](https://spring-boot-planner.onrender.com)

<p align="center">
	<img src="src/main/resources/static/images/dashboard.png" alt="Dashboard" width="500" style="max-width: 100%;" />
</p>
<br />
<p align="center">
	<img src="src/main/resources/static/images/horario.png" alt="Horario semanal" width="500" style="max-width: 100%;" />
</p>
<br />
<p align="center">
	<img src="src/main/resources/static/images/weekly-planner.png" alt="Planificación semanal" width="500" style="max-width: 100%;" />
</p>
<br />

### Limitaciones de la demo en producción

En el despliegue público en Render (plan gratuito), la **recuperación de contraseña por email no está disponible**: estos entornos suelen bloquear conexiones SMTP salientes (en este caso, Gmail en el puerto 587), lo que impide enviar el correo con el enlace de restablecimiento. El resto de funcionalidades (registro, login, planificación, CRUD, etc.) funciona con normalidad.

En **entorno local**, con las variables de correo configuradas, el flujo de recuperación sí puede probarse de extremo a extremo.

## Funcionalidades actuales

- Autenticación de usuarios con Spring Security.
- Login y logout.
- Recuperación de contraseña por email con token temporal (válida en local, ver limitaciones de la demo).
- Gestión de perfil de usuario.
- Panel de administración de usuarios (`/admin/usuarios`) para cuentas con rol `ADMIN`.
- CRUD de:
  - Asignaturas
  - Profesores
  - Tareas
  - Exámenes
  - Horarios de clase
  - Bloques de estudio
- Dashboard con:
  - Calendario mensual
  - Planificación de hoy
  - Próximos exámenes
  - Próximas entregas
  - Imagen inspiradora
- Planificación semanal de bloques de estudio (`/bloquesEstudio`):
  - Vista por días de la semana con navegación entre semanas
  - Arrastrar y soltar para **mover** un bloque a otro día
  - Botón para **copiar** un bloque a otro día (diálogo de selección; usable en móvil)
  - Validación de solapamientos de horario al mover o copiar
- Vista diaria de bloques accesible desde el calendario del dashboard.
- Gestión de imágenes inspiradoras por usuario:
  - Subida de imágenes a Cloudinary
  - Persistencia de URL y `publicId`
  - Borrado de imagen (Cloudinary + base de datos)
- Fondo del dashboard dinámico por sesión (si hay imágenes disponibles).

## Tecnologías

- Java 17
- Spring Boot
- Spring MVC
- Spring Data JPA
- Spring Data REST
- Spring Security
- Thymeleaf
- PostgreSQL (Supabase)
- Cloudinary
- Maven
- FullCalendar (frontend)

## Estructura del proyecto

- `src/main/java/com/planner/spring_boot_planner/`
  - `config/` — Configuración general (`SecurityConfig`, `CloudinaryConfig`, `RestConfig`, etc.)
  - `controller/` — Controladores web por módulo (dashboard, tareas, exámenes, usuarios, imágenes, bloques de estudio, etc.)
  - `entity/` — Entidades JPA
  - `repository/` — Repositorios Spring Data JPA / Data REST
  - `service/` — Servicios de dominio e integración externa (Cloudinary, correo, tokens de recuperación)
- `src/main/resources/`
  - `application.properties`
  - `static/` — `css/styles.css`, `javascript/script.js`, imágenes
  - `templates/` — Vistas Thymeleaf por módulo (`dashboard`, `tareas`, `examenes`, `horariosClase`, `bloquesEstudio`, `imagenes`, etc.)
- `Dockerfile` — Imagen para despliegue en PaaS (Render)

## Configuración de entorno

La aplicación usa variables de entorno para credenciales y secretos. En local puedes definirlas en `src/main/resources/application-local.properties` (archivo opcional, importado automáticamente).

### Variables requeridas

| Variable | Descripción |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL (Supabase) |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos |
| `SPRING_MAIL_USERNAME` | Cuenta SMTP (p. ej. Gmail) para recuperación de contraseña |
| `SPRING_MAIL_PASSWORD` | Contraseña de aplicación SMTP |
| `CLOUDINARY_CLOUD_NAME` | Cloud name de Cloudinary |
| `CLOUDINARY_API_KEY` | API key de Cloudinary |
| `CLOUDINARY_API_SECRET` | API secret de Cloudinary |

### Variables opcionales

| Variable | Descripción |
|----------|-------------|
| `APP_ADMIN_EMAIL` | Email que recibe rol `ADMIN` al registrarse o iniciar sesión |
| `PORT` | Puerto del servidor (por defecto `8080`; Render lo inyecta automáticamente) |
| `APP_BASE_URL` | URL pública de la app (p. ej. `https://spring-boot-planner.onrender.com`), usada en los enlaces del correo de recuperación. En local: `http://localhost:8080` |

Para recuperación de contraseña en local, en `application-local.properties`:

```properties
app.base-url=http://localhost:8080
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
```

(o define directamente `SPRING_MAIL_USERNAME` y `SPRING_MAIL_PASSWORD` como variables de entorno).

## Ejecución en local

1. Clona el repositorio.
2. Crea `src/main/resources/application-local.properties` con las variables anteriores (sin subir credenciales al repositorio).
3. Asegúrate de tener Java 17 instalado.
4. Ejecuta:

```bash
./mvnw spring-boot:run
```

5. Abre [http://localhost:8080](http://localhost:8080).

## Despliegue

El proyecto incluye un `Dockerfile` que ejecuta `./mvnw clean package -DskipTests` y arranca el JAR en el puerto definido por `PORT`.

**Render** (demo actual): conectar el repositorio, configurar las variables de entorno y desplegar como Web Service. El pool de conexiones Hikari está limitado a 5 conexiones para adaptarse a límites de Supabase en planes gratuitos.

## Autor

M. Paz Jiménez Martín
