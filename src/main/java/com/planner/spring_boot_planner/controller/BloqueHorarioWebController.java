package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.BloqueHorario;
import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.repository.BloqueHorarioRepository;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bloquesHorario")
public class BloqueHorarioWebController {

	private final BloqueHorarioRepository bloqueHorarioRepository;
	private final AsignaturaRepository asignaturaRepository;

	public BloqueHorarioWebController(BloqueHorarioRepository bloqueHorarioRepository,
								AsignaturaRepository asignaturaRepository) {
		this.bloqueHorarioRepository = bloqueHorarioRepository;
		this.asignaturaRepository = asignaturaRepository;
	}

	@GetMapping()
	public String listarBloques(Model model) {
		model.addAttribute("bloquesHorario", bloqueHorarioRepository.findAll());
		return "bloquesHorario/HorarioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("bloqueHorario", new BloqueHorario());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "bloquesHorario/HorarioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("bloqueHorario") BloqueHorario bloqueHorario,
							   BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "bloquesHorario/HorarioFormView";
		}
		resolverAsignatura(bloqueHorario);
		bloqueHorarioRepository.save(bloqueHorario);
		return "redirect:/bloquesHorario";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		BloqueHorario bloqueHorario = bloqueHorarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horario no encontrado: " + id));
		model.addAttribute("bloqueHorario", bloqueHorario);
		model.addAttribute("asignatura", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "bloquesHorario/formView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("bloqueHorario") BloqueHorario bloqueHorario,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "bloquesHorario/BloqueHorarioFormView";
		}
		bloqueHorario.setId(id);
		resolverAsignatura(bloqueHorario);
		bloqueHorarioRepository.save(bloqueHorario);
		return "redirect:/bloquesHorario";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar8(@PathVariable Long id) {
		bloqueHorarioRepository.deleteById(id);
		return "redirect:/bloquesHorario";
	}


	private void resolverAsignatura(BloqueHorario bloqueHorario) {
		if (bloqueHorario.getAsignatura() != null && bloqueHorario.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(bloqueHorario.getAsignatura().getId())
				.orElse(null);
			bloqueHorario.setAsignatura(asignatura);
		} else {
			bloqueHorario.setAsignatura(null);
		}
	}
}
