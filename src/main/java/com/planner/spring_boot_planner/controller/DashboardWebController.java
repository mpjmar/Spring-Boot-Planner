package com.planner.spring_boot_planner.controller;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.planner.spring_boot_planner.DiaLectivo;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.entity.Examen;
import com.planner.spring_boot_planner.entity.HorarioClase;
import com.planner.spring_boot_planner.entity.Tarea;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import com.planner.spring_boot_planner.repository.ExamenRepository;
import com.planner.spring_boot_planner.repository.HorarioClaseRepository;
import com.planner.spring_boot_planner.repository.TareaRepository;

@Controller
public class DashboardWebController {

	private final TareaRepository tareaRepository;
	private final ExamenRepository examenRepository;
	private final BloqueEstudioRepository bloqueEstudioRepository;
	private final HorarioClaseRepository horarioClaseRepository;

	public DashboardWebController(
		TareaRepository tareaRepository,
		ExamenRepository examenRepository,
		BloqueEstudioRepository bloqueEstudioRepository,
		HorarioClaseRepository horarioClaseRepository
	) {
		this.tareaRepository = tareaRepository;
		this.examenRepository = examenRepository;
		this.bloqueEstudioRepository = bloqueEstudioRepository;
		this.horarioClaseRepository = horarioClaseRepository;
	}

	@GetMapping("/dashboard")
	public String mostrarDashboard(@AuthenticationPrincipal Usuario usuario, Model model) {
		LocalDate hoy = LocalDate.now();
		LocalDate enDosMeses = hoy.plusMonths(2);

		List<Tarea> tareas = tareaRepository.findByUsuarioId(usuario.getId()).stream()
			.filter(tarea -> tarea.getFechaLimite() != null)
			.sorted(Comparator.comparing(Tarea::getFechaLimite))
			.toList();

		List<Examen> examenes = examenRepository.findByUsuarioId(usuario.getId()).stream()
			.filter(examen -> examen.getFecha() != null)
			.sorted(Comparator.comparing(Examen::getFecha))
			.toList();

		List<CalendarEventItem> eventosCalendario = new ArrayList<>();
		for (Tarea tarea : tareas) {
			if (!tarea.getFechaLimite().isBefore(hoy) && !tarea.getFechaLimite().isAfter(enDosMeses)) {
				String asignatura = tarea.getAsignatura() != null ? tarea.getAsignatura().getNombre() : "General";
				String color = tarea.getColor() != null ? tarea.getColor() : "#2563eb";
				eventosCalendario.add(new CalendarEventItem(
					"Entrega: " + tarea.getNombre(),
					tarea.getFechaLimite().toString(),
					color,
					"/tareas/" + tarea.getId() + "/editar",
					"Tarea - " + asignatura));
			}
		}

		for (Examen examen : examenes) {
			if (!examen.getFecha().isBefore(hoy) && !examen.getFecha().isAfter(enDosMeses)) {
				String asignatura = examen.getAsignatura() != null ? examen.getAsignatura().getNombre() : "General";
				eventosCalendario.add(new CalendarEventItem(
					"Examen: " + asignatura,
					examen.getFecha().toString(),
					"#dc2626",
					"/examenes/" + examen.getId() + "/editar",
					"Examen - " + examen.getDescripcion()));
			}
		}

		List<Tarea> proximasTareas = tareas.stream()
			.filter(tarea -> !tarea.getFechaLimite().isBefore(hoy))
			.limit(5)
			.toList();

		List<Examen> proximosExamenes = examenes.stream()
			.filter(examen -> !examen.getFecha().isBefore(hoy))
			.limit(5)
			.toList();

		model.addAttribute("eventosCalendario", eventosCalendario);
		model.addAttribute("proximasTareas", proximasTareas);
		model.addAttribute("proximosExamenes", proximosExamenes);
		model.addAttribute("planificacionHoy", construirPlanificacionHoy(usuario, hoy));
		List<String> imagenesInspiradoras = listarImagenesInspiradoras();
		model.addAttribute("imagenesInspiradoras", imagenesInspiradoras);
		model.addAttribute("imagenInspiradora", seleccionarImagenInspiradora(imagenesInspiradoras));
		return "dashboard";
	}

	private List<PlanificacionItem> construirPlanificacionHoy(Usuario usuario, LocalDate hoy) {
		List<PlanificacionItem> items = new ArrayList<>();

		List<BloqueEstudio> bloques = bloqueEstudioRepository.findByFechaAndUsuarioId(hoy, usuario.getId());
		for (BloqueEstudio bloque : bloques) {
			String nombre = (bloque.getAsignatura() != null) ? bloque.getAsignatura().getNombre() : "Bloque de estudio";
			String color = (bloque.getColor() != null) ? bloque.getColor() : "#2563eb";
			items.add(new PlanificacionItem(
				"Estudio",
				bloque.getHoraInicio(),
				bloque.getHoraFin(),
				nombre,
				color,
				"/bloquesEstudio"
			));
		}

		DiaLectivo diaLectivo = mapearDiaLectivo(hoy.getDayOfWeek());
		if (diaLectivo != null) {
			List<HorarioClase> horarios = horarioClaseRepository.findByDiaLectivoAndUsuarioId(diaLectivo, usuario.getId());
			for (HorarioClase horario : horarios) {
				String nombre = (horario.getAsignatura() != null) ? horario.getAsignatura().getNombre() : "Clase";
				String color = (horario.getAsignatura() != null && horario.getAsignatura().getColor() != null)
					? horario.getAsignatura().getColor()
					: "#059669";
				items.add(new PlanificacionItem(
					"Clase",
					horario.getHoraInicio(),
					horario.getHoraFin(),
					nombre,
					color,
					"/horariosClase"
				));
			}
		}

		items.sort(Comparator.comparing(PlanificacionItem::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())));
		return items;
	}

	private DiaLectivo mapearDiaLectivo(DayOfWeek dayOfWeek) {
		if (dayOfWeek == null) {
			return null;
		}
		return switch (dayOfWeek) {
			case MONDAY -> DiaLectivo.LUNES;
			case TUESDAY -> DiaLectivo.MARTES;
			case WEDNESDAY -> DiaLectivo.MIERCOLES;
			case THURSDAY -> DiaLectivo.JUEVES;
			case FRIDAY -> DiaLectivo.VIERNES;
			default -> null;
		};
	}

	public static class PlanificacionItem {
		private final String tipo;
		private final java.time.LocalTime horaInicio;
		private final java.time.LocalTime horaFin;
		private final String titulo;
		private final String color;
		private final String url;

		public PlanificacionItem(String tipo, java.time.LocalTime horaInicio, java.time.LocalTime horaFin, String titulo, String color, String url) {
			this.tipo = tipo;
			this.horaInicio = horaInicio;
			this.horaFin = horaFin;
			this.titulo = titulo;
			this.color = color;
			this.url = url;
		}

		public String getTipo() { return tipo; }
		public java.time.LocalTime getHoraInicio() { return horaInicio; }
		public java.time.LocalTime getHoraFin() { return horaFin; }
		public String getTitulo() { return titulo; }
		public String getColor() { return color; }
		public String getUrl() { return url; }
	}

	private String seleccionarImagenInspiradora(List<String> imagenes) {
		if (imagenes == null || imagenes.isEmpty()) {
			return null;
		}
		int index = ThreadLocalRandom.current().nextInt(imagenes.size());
		return imagenes.get(index);
	}

	private List<String> listarImagenesInspiradoras() {
		Path imagesDir = Path.of("src/main/resources/static/images");
		if (!Files.isDirectory(imagesDir)) {
			return List.of();
		}

		try (Stream<Path> files = Files.list(imagesDir)) {
			return files
				.filter(Files::isRegularFile)
				.map(path -> path.getFileName().toString())
				.filter(this::esImagen)
				.map(nombre -> "/images/" + nombre)
				.sorted()
				.toList();
		} catch (Exception ex) {
			return List.of();
		}
	}

	private boolean esImagen(String nombreArchivo) {
		String lower = nombreArchivo.toLowerCase();
		return Arrays.asList(".png", ".jpg", ".jpeg", ".webp", ".gif")
			.stream()
			.anyMatch(lower::endsWith);
	}

	public static class CalendarEventItem {
		private final String title;
		private final String start;
		private final String color;
		private final String url;
		private final String extendedHint;

		public CalendarEventItem(String title, String start, String color, String url, String extendedHint) {
			this.title = title;
			this.start = start;
			this.color = color;
			this.url = url;
			this.extendedHint = extendedHint;
		}

		public String getTitle() {
			return title;
		}

		public String getStart() {
			return start;
		}

		public String getColor() {
			return color;
		}

		public String getUrl() {
			return url;
		}

		public String getExtendedHint() {
			return extendedHint;
		}
	}
}
