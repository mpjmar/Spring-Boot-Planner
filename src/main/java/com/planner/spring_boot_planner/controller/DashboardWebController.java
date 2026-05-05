package com.planner.spring_boot_planner.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.planner.spring_boot_planner.entity.Examen;
import com.planner.spring_boot_planner.entity.Tarea;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.ExamenRepository;
import com.planner.spring_boot_planner.repository.TareaRepository;

@Controller
public class DashboardWebController {

	private final TareaRepository tareaRepository;
	private final ExamenRepository examenRepository;

	public DashboardWebController(TareaRepository tareaRepository, ExamenRepository examenRepository) {
		this.tareaRepository = tareaRepository;
		this.examenRepository = examenRepository;
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
		return "dashboard";
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
