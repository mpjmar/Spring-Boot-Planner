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
import com.planner.spring_boot_planner.entity.HorarioClase;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.HorarioClaseRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/horarioClase")
public class HorarioClaseWebController {

	private final HorarioClaseRepository horarioClaseRepository;
	private final AsignaturaRepository asignaturaRepository;

	public HorarioClaseWebController(HorarioClaseRepository horarioClaseRepository,
								AsignaturaRepository asignaturaRepository) {
		this.horarioClaseRepository = horarioClaseRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping()
	public String listarBloques(Model model) {
		model.addAttribute("horarioClase", horarioClaseRepository.findAll());
		return "horarioClase/horarioClaseView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("horarioClase", new HorarioClase());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "horarioClase/horarioClaseFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
							   BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "horarioClase/horarioClaseFormView";
		}
		resolverAsignatura(horarioClase);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horarioClase";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horarioClase no encontrado: " + id));
		model.addAttribute("horarioClase", horarioClase);
		model.addAttribute("asignatura", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "horarioClase/formView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "horarioClase/horarioClaseFormView";
		}
		horarioClase.setId(id);
		resolverAsignatura(horarioClase);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horarioClase";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar8(@PathVariable Long id) {
		horarioClaseRepository.deleteById(id);
		return "redirect:/horarioClase";
	}


	private void resolverAsignatura(HorarioClase horarioClase) {
		if (horarioClase.getAsignatura() != null && horarioClase.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(horarioClase.getAsignatura().getId())
				.orElse(null);
			horarioClase.setAsignatura(asignatura);
		} else {
			horarioClase.setAsignatura(null);
		}
	}
}
