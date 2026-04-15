# Planificador de estudio

Aplicación web desarrollada con Spring Boot para organizar el estudio semanal, gestionar tareas y exámenes, y visualizar la carga académica de forma clara.

## Objetivo

Crear una herramienta útil para estudiantes que permita:

- planificar el estudio por asignaturas
- controlar tareas, fechas límite y estado
- registrar exámenes y su peso
- organizar bloques semanales de estudio
- ver un resumen de progreso en un panel principal

## Tecnologías

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Thymeleaf
- Maven
- Base de datos relacional (PostgreSQL)
- Cloudinary (almacenamiento de imágenes)

## Funcionalidades principales (MVP)

1. Gestión de asignaturas
- Alta, edición, listado y borrado.
- Campos: nombre, profesor, dificultad, horas objetivo por semana, imagenUrl.

2. Gestión de profesores
- Alta, edición, listado y borrado.
- Campos: nombre, email.

3. Gestión de tareas
- Alta, edición, listado y borrado.
- Campos: título, descripción, fecha límite, prioridad, estado, horas estimadas.
- Relación con asignatura.

4. Gestión de exámenes
- Alta, edición, listado y borrado.
- Campos: título, fecha, peso, nota objetivo.
- Relación con asignatura.

5. Gestión de bloques de estudio
- Alta, edición, listado y borrado.
- Campos: día de semana, hora inicio, hora fin, tipo de sesión, completado.
- Relación con asignatura.

6. Dashboard
- Tareas pendientes.
- Tareas vencidas.
- Exámenes próximos.
- Carga semanal por asignatura.

## Estructura propuesta del proyecto

src/main/java/com/usuario/planificadorestudio
- PlanificadorDeEstudioApplication.java
- controller
  - AsignaturaWebController.java
  - ProfesorWebController.java
  - TareaWebController.java
  - ExamenWebController.java
  - BloqueEstudioWebController.java
  - DashboardWebController.java
- config
  - RestConfig.java
  - CloudinaryConfig.java
- entity
  - Asignatura.java
  - Profesor.java
  - Tarea.java
  - Examen.java
  - BloqueEstudio.java
  - EstadoTarea.java
  - TipoSesion.java
- repository
  - AsignaturaRepository.java
  - ProfesorRepository.java
  - TareaRepository.java
  - ExamenRepository.java
  - BloqueEstudioRepository.java
- service
  - PlanificacionService.java
  - CloudinaryService.java

src/main/resources
- application.properties
- static
  - css
- templates
  - dashboard.html
  - asignaturas
    - AsignaturaListingView.html
    - AsignaturaFormView.html
  - tareas
    - TareaListingView.html
    - TareaFormView.html
  - examenes
    - ExamenListingView.html
    - ExamenFormView.html
  - bloques
    - BloqueListingView.html
    - BloqueFormView.html

## Recursos estáticos

La aplicación organiza los estilos frontend en:

- src/main/resources/static/css

Las plantillas Thymeleaf deben referenciar estos recursos desde /css.

## Paquete config

El paquete config centraliza la configuración técnica de la aplicación.

- RestConfig
  - Define la base de la API en /api.
  - Expone los IDs de entidades en Spring Data REST.
  - Configura CORS para los endpoints de API.

- CloudinaryConfig
  - Crea el bean Cloudinary con credenciales externas.
  - Permite reutilizar la integración desde servicios de subida de imagen.

## Imágenes de asignaturas con Cloudinary

Para almacenar las imágenes de las asignaturas se utiliza Cloudinary.

Flujo de integración:

1. Dependencia Maven
- Añadir en pom.xml:
  - com.cloudinary:cloudinary-http44:1.39.0

2. Configuración de credenciales
- Definir en application-local.properties (sin versionar secretos):
  - cloudinary.cloud-name=TU_CLOUD_NAME
  - cloudinary.api-key=TU_API_KEY
  - cloudinary.api-secret=TU_API_SECRET
- Mantener en application.properties:
  - spring.config.import=optional:application-local.properties

3. Bean y servicio
- Crear CloudinaryConfig para exponer el bean Cloudinary.
- Crear CloudinaryService con un método tipo subirImagen(MultipartFile archivo, String carpeta) que devuelva una URL segura.

4. Formularios Thymeleaf
- En el formulario de asignatura usar:
  - enctype="multipart/form-data"
  - input file con name="imagen" y accept="image/*"

5. Controlador web de asignatura
- En POST /asignaturas/nuevo y POST /asignaturas/{id}/editar:
  - Recibir @RequestParam("imagen") MultipartFile imagen
  - Subir imagen con CloudinaryService si no está vacía
  - Guardar la URL en asignatura.setImagenUrl(...)
  - En edición, conservar la URL anterior si no se sube una nueva imagen

6. Límite de subida
- Configurar tamaño máximo multipart en application.properties (por ejemplo 20MB).

En listados, mostrar un placeholder cuando imagenUrl sea null para evitar celdas vacías.

## Modelo de datos resumido

Asignatura
- id
- nombre
- profesor
- dificultad
- horasObjetivoSemana
- imagenUrl

Profesor
- id
- nombre
- email
- asignaturas

Tarea
- id
- título
- descripción
- fechaLímite
- prioridad
- estado
- horasEstimadas
- asignatura

Examen
- id
- título
- fecha
- peso
- notaObjetivo
- asignatura

BloqueEstudio
- id
- díaSemana
- horaInicio
- horaFin
- tipoSesión
- completado
- asignatura

## Lógica de negocio prevista

- detectar tareas vencidas automáticamente
- calcular carga semanal por asignatura
- priorizar tareas por urgencia y prioridad
- validar que no haya solapamiento de bloques de estudio
- mostrar alertas de exámenes cercanos

## Validaciones recomendadas

- campos obligatorios en formularios principales
- prioridad entre 1 y 5
- dificultad entre 1 y 5
- hora de inicio menor que hora de fin
- fecha de examen obligatoria
- relación válida con asignatura al guardar tarea, examen o bloque

## Hoja de ruta (checklist)

Fase 1
- Crear entidades y repositorios.
- Configurar base de datos y probar conexión.

Fase 2
- Implementar CRUD de asignaturas.
- Implementar CRUD de tareas.

Fase 3
- Implementar CRUD de exámenes.
- Implementar CRUD de bloques de estudio.

Fase 4
- Crear servicio de planificación con reglas de negocio.
- Crear dashboard con métricas principales.

Fase 5
- Revisar validaciones y errores.
- Mejorar interfaz.
- Preparar demo y defensa final.

## Cómo ejecutar

1. Configurar la base de datos en application.properties.
2. Ejecutar la aplicación con Maven Wrapper.
3. Abrir navegador en localhost puerto configurado.
4. Acceder al dashboard y empezar a crear datos.

## Posibles mejoras futuras

- autenticación de usuario
- recordatorios por correo
- exportación a PDF del plan semanal
- estadísticas por períodos
- versión responsive más completa para móvil

## Autoría

Proyecto académico de DAM  
Nombre del proyecto: Planificador de estudio  
Curso: 2025-2026
Autor: M. Paz Jiménez Martín