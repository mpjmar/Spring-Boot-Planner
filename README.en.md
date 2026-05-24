# Spring Boot Planner

Academic planning web application built with Spring Boot + Thymeleaf.

Manage your studies and workload through modules for subjects, tasks, exams, class schedules, and study blocks, with a central dashboard for an overview of your progress.

**Languages:** [Español](README.md) · English

## Demo

- **Live app:** [https://spring-boot-planner.onrender.com](https://spring-boot-planner.onrender.com)

<p align="center">
	<img src="src/main/resources/static/images/dashboard.png" alt="Dashboard" width="500" style="max-width: 100%;" />
</p>
<br />
<p align="center">
	<img src="src/main/resources/static/images/horario.png" alt="Weekly schedule" width="500" style="max-width: 100%;" />
</p>
<br />
<p align="center">
	<img src="src/main/resources/static/images/weekly-planner.png" alt="Weekly study planning" width="500" style="max-width: 100%;" />
</p>
<br />

### Production demo limitations

On the public deployment on Render (free tier), **password recovery by email is not available**: these environments often block outbound SMTP connections (e.g. Gmail on port 587), which prevents sending the email with the reset link. All other features (registration, login, planning, CRUD, etc.) work as expected.

In a **local environment**, with mail variables configured, the full recovery flow can be tested end to end.

## Current features

- User authentication with Spring Security.
- Login and logout.
- Password recovery by email with a temporary token (works locally; see demo limitations).
- User profile management.
- User administration panel (`/admin/usuarios`) for accounts with the `ADMIN` role.
- CRUD for:
  - Subjects
  - Teachers
  - Tasks
  - Exams
  - Class schedules
  - Study blocks
- Dashboard with:
  - Monthly calendar
  - Today's plan
  - Upcoming exams
  - Upcoming deadlines
  - Inspirational image
- Weekly study block planning (`/bloquesEstudio`):
  - Week view by day with week navigation
  - Drag and drop to **move** a block to another day
  - Button to **copy** a block to another day (selection dialog; mobile-friendly)
  - Time overlap validation when moving or copying
- Daily block view accessible from the dashboard calendar.
- Per-user inspirational image management:
  - Image upload to Cloudinary
  - URL and `publicId` persistence
  - Image deletion (Cloudinary + database)
- Dynamic dashboard background per session (when images are available).

## Technologies

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

## Project structure

- `src/main/java/com/planner/spring_boot_planner/`
  - `config/` — General configuration (`SecurityConfig`, `CloudinaryConfig`, `RestConfig`, etc.)
  - `controller/` — Web controllers per module (dashboard, tasks, exams, users, images, study blocks, etc.)
  - `entity/` — JPA entities
  - `repository/` — Spring Data JPA / Data REST repositories
  - `service/` — Domain services and external integrations (Cloudinary, mail, recovery tokens)
- `src/main/resources/`
  - `application.properties`
  - `static/` — `css/styles.css`, `javascript/script.js`, images
  - `templates/` — Thymeleaf views per module (`dashboard`, `tareas`, `examenes`, `horariosClase`, `bloquesEstudio`, `imagenes`, etc.)
- `Dockerfile` — Image for PaaS deployment (Render, Railway, etc.)

## Environment configuration

The application uses environment variables for credentials and secrets. Locally, you can define them in `src/main/resources/application-local.properties` (optional file, imported automatically).

### Required variables

| Variable | Description |
|----------|-------------|
| `SPRING_DATASOURCE_URL` | PostgreSQL JDBC URL (Supabase) |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `SPRING_MAIL_USERNAME` | SMTP account (e.g. Gmail) for password recovery |
| `SPRING_MAIL_PASSWORD` | SMTP application password |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary cloud name |
| `CLOUDINARY_API_KEY` | Cloudinary API key |
| `CLOUDINARY_API_SECRET` | Cloudinary API secret |

### Optional variables

| Variable | Description |
|----------|-------------|
| `APP_ADMIN_EMAIL` | Email that receives the `ADMIN` role on registration or login |
| `PORT` | Server port (default `8080`; Render injects it automatically) |
| `APP_BASE_URL` | Public app URL (e.g. `https://spring-boot-planner.onrender.com`), used in password recovery email links. Local: `http://localhost:8080` |

For password recovery locally, in `application-local.properties`:

```properties
app.base-url=http://localhost:8080
spring.mail.username=${SPRING_MAIL_USERNAME}
spring.mail.password=${SPRING_MAIL_PASSWORD}
```

(or set `SPRING_MAIL_USERNAME` and `SPRING_MAIL_PASSWORD` directly as environment variables).

## Running locally

1. Clone the repository.
2. Create `src/main/resources/application-local.properties` with the variables above (do not commit credentials).
3. Ensure Java 17 is installed.
4. Run:

```bash
./mvnw spring-boot:run
```

5. Open [http://localhost:8080](http://localhost:8080).

## Deployment

The project includes a `Dockerfile` that runs `./mvnw clean package -DskipTests` and starts the JAR on the port defined by `PORT`.

**Render** (current demo): connect the repository, configure environment variables, and deploy as a Web Service. The Hikari connection pool is limited to 5 connections to fit Supabase free-tier limits.

**Production notes:**

- Set `APP_BASE_URL` to the exact public URL (no trailing slash) so password recovery links point to the correct domain when mail can be sent.
- On Render's free tier, SMTP to Gmail is usually unavailable; enabling mail in production would require a transactional provider (Brevo, SendGrid, Resend, etc.).

## Author

M. Paz Jiménez Martín
