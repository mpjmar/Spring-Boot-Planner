package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.Profesor;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.ProfesorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller 
@RequestMapping("/asignaturas")
public class AsignaturaWebController {

	private final AsignaturaRepository asignaturaRepository;
	private final ProfesorRepository profesorRepository;

	public AsignaturaWebController(AsignaturaRepository asignaturaRepository, 
								   ProfesorRepository profesorRepository) {
		this.asignaturaRepository = asignaturaRepository;
		this.profesorRepository = profesorRepository;
	}

	@GetMapping
	public String listarAsignaturas(Model model) {
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		return "asignaturas/AsignaturaListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("asignatura", new Asignatura());
		model.addAttribute("profesores", profesorRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "asignaturas/AsignaturaFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("asignatura") Asignatura asignatura,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("profesores", profesorRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "asignaturas/AsignaturaFormView";
		}
		resolverProfesor(asignatura);
		asignaturaRepository.save(asignatura);
		return "redirect:/asignaturas";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		Asignatura asignatura = asignaturaRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada: " + id));
		model.addAttribute("asignatura", asignatura);
		model.addAttribute("profesores", profesorRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "asignaturas/AsignaturaFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("asignatura") Asignatura asignatura,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "asignaturas/AsignaturaFormView";
		}
		asignatura.setId(id);
		resolverProfesor(asignatura);
		asignaturaRepository.save(asignatura);
		return "redirect:/asignaturas";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable Long id) {
		asignaturaRepository.deleteById(id);
		return "redirect:/asignaturas";
	}

	private void resolverProfesor(Asignatura asignatura) {
		if (asignatura.getProfesor() != null && asignatura.getProfesor().getId() != null) {
			Profesor profesor = profesorRepository.findById(asignatura.getProfesor().getId())
				.orElse(null);
			asignatura.setProfesor(profesor);
		} else {
			asignatura.setProfesor(null);
		}
	}
}
