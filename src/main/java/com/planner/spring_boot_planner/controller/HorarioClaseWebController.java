package com.planner.spring_boot_planner.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.HorarioClase;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.HorarioClaseRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/horariosClase")
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
		return "horariosClase/HorarioClaseListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("horarioClase", new HorarioClase());
		model.addAttribute("asignaturas", asignaturaRepository.findAll());
		model.addAttribute("accion", "Añadir");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
							   BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("asignaturas", asignaturaRepository.findAll());
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}
		resolverAsignatura(horarioClase);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horariosClase";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horarioClase no encontrado: " + id));
		model.addAttribute("horarioClase", horarioClase);
		model.addAttribute("asignatura", asignaturaRepository.findAll());
		model.addAttribute("accion", "Editar");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}
		horarioClase.setId(id);
		resolverAsignatura(horarioClase);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horariosClase";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar8(@PathVariable Long id) {
		horarioClaseRepository.deleteById(id);
		return "redirect:/horariosClase";
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

	@GetMapping("/dia")
	public String verDia(@RequestParam(required = false) String fecha, Model model) {
		LocalDate dia;
		if (fecha == null) {
			dia = LocalDate.now();
		} else {
			dia = LocalDate.parse(fecha);
		}
		List<HorarioClase> horarios = horarioClaseRepository.findByFecha(dia);
		horarios.sort(Comparator.comparing(HorarioClase::getHoraInicio));
		model.addAttribute("horarios", horarios);
		model.addAttribute("fecha", dia);
		return "horariosClase/HorarioClaseDayView";
	}
}
