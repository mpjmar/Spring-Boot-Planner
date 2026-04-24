package com.planner.spring_boot_planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.Tarea;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.TareaRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/tareas")
public class TareaWebController {

	private final TareaRepository tareaRepository;
	private final AsignaturaRepository asignaturaRepository;

	public TareaWebController(TareaRepository tareaRepository, 
							  AsignaturaRepository asignaturaRepository) {
		this.tareaRepository = tareaRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping
	public String listarTareas(Model model) {
		model.addAttribute("tareas", tareaRepository.findAll());
		return "tareas/TareaListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("tarea", new Tarea());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "tareas/TareaFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("tarea") Tarea tarea,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "tareas/TareaFormView";
		}
		// Construye Duration a partir de horas y minutos
		int horas = tarea.getHoras() != null ? tarea.getHoras() : 0;
		int minutos = tarea.getMinutos() != null ? tarea.getMinutos() : 0;
		tarea.setTiempoEstimado(java.time.Duration.ofHours(horas).plusMinutes(minutos));
		resolverAsignatura(tarea);
		tareaRepository.save(tarea);
		return "redirect:/tareas";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		Tarea tarea = tareaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Tarea no encontrada: " + id));
		model.addAttribute("tarea", tarea);
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "tareas/TareaFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("tarea") Tarea tarea,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "tareas/TareaFormView";
		}
		tarea.setId(id);
		// Construye Duration a partir de horas y minutos
		int horas = tarea.getHoras() != null ? tarea.getHoras() : 0;
		int minutos = tarea.getMinutos() != null ? tarea.getMinutos() : 0;
		tarea.setTiempoEstimado(java.time.Duration.ofHours(horas).plusMinutes(minutos));
		resolverAsignatura(tarea);
		tareaRepository.save(tarea);
		return "redirect:/tareas";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id) {
		tareaRepository.deleteById(id);
		return "redirect:/tareas";
	}
	

	private void resolverAsignatura(Tarea tarea) {
		if (tarea.getAsignatura() != null && tarea.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(tarea.getAsignatura().getId())
				.orElse(null);
			tarea.setAsignatura(asignatura);
			if (asignatura != null) {
				tarea.setColor(asignatura.getColor());
			}
		} else {
			tarea.setAsignatura(null);
			tarea.setColor(null);
		}
	}

}
