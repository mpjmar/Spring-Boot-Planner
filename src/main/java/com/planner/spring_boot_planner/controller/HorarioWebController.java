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
import com.planner.spring_boot_planner.entity.Horario;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.HorarioRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/horario")
public class HorarioWebController {

	private final HorarioRepository horarioRepository;
	private final AsignaturaRepository asignaturaRepository;

	public HorarioWebController(HorarioRepository horarioRepository,
								AsignaturaRepository asignaturaRepository) {
		this.horarioRepository = horarioRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping()
	public String listarBloques(Model model) {
		model.addAttribute("horario", horarioRepository.findAll());
		return "horario/HorarioView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("horario", new Horario());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "horario/HorarioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horario") Horario horario,
							   BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "horario/HorarioFormView";
		}
		resolverAsignatura(horario);
		horarioRepository.save(horario);
		return "redirect:/horario";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		Horario horario = horarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horario no encontrado: " + id));
		model.addAttribute("horario", horario);
		model.addAttribute("asignatura", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "horario/formView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("horario") Horario horario,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "horario/horarioFormView";
		}
		horario.setId(id);
		resolverAsignatura(horario);
		horarioRepository.save(horario);
		return "redirect:/horario";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar8(@PathVariable Long id) {
		horarioRepository.deleteById(id);
		return "redirect:/horario";
	}


	private void resolverAsignatura(Horario horario) {
		if (horario.getAsignatura() != null && horario.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(horario.getAsignatura().getId())
				.orElse(null);
			horario.setAsignatura(asignatura);
		} else {
			horario.setAsignatura(null);
		}
	}
}
