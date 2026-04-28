package com.planner.spring_boot_planner.controller;

import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.planner.spring_boot_planner.entity.Profesor;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.AsignaturaRepository;
import com.planner.spring_boot_planner.repository.HorarioClaseRepository;
import com.planner.spring_boot_planner.repository.ProfesorRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/horariosClase")
public class HorarioClaseWebController {

	private final HorarioClaseRepository horarioClaseRepository;
	private final AsignaturaRepository asignaturaRepository;
	private final ProfesorRepository profesorRepository;

	public HorarioClaseWebController(HorarioClaseRepository horarioClaseRepository,
								     AsignaturaRepository asignaturaRepository,
									 ProfesorRepository profesorRepository) {
		this.horarioClaseRepository = horarioClaseRepository;
		this.asignaturaRepository = asignaturaRepository;
		this.profesorRepository = profesorRepository;
	}

	@GetMapping()
	public String listarHorarios(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("horariosClase", horarioClaseRepository.findByUsuarioId(usuario.getId()));
		return "horariosClase/HorarioClaseListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("horarioClase", new HorarioClase());
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Añadir");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
							   BindingResult result, Model model, 
							   @AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}
		resolverAsignatura(horarioClase, usuario);
		resolverProfesor(horarioClase, usuario);
		horarioClase.setUsuario(usuario);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horariosClase";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horarioClase no encontrado: " + id));
		if (!puedeGestionar(horarioClase, usuario))
			return "redirect:/horariosClase";
		model.addAttribute("horarioClase", horarioClase);
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Editar");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable Long id,
								@Valid @ModelAttribute("horarioClase") HorarioClase horarioClase,
								BindingResult result, Model model, 
								@AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}
		HorarioClase existente = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Horario de clase no encontrado: " + id));
		if (!puedeGestionar(existente, usuario))
			return "redirect:/horariosClase";
		horarioClase.setId(id);
		horarioClase.setUsuario(usuario);
		resolverAsignatura(horarioClase, usuario);
		resolverProfesor(horarioClase, usuario);
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horariosClase";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar8(@PathVariable Long id, 
							@AuthenticationPrincipal Usuario usuario) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id).orElse(null);
		if (horarioClase == null || !puedeGestionar(horarioClase, usuario))
			return "redirect:/horariosClase";
		horarioClaseRepository.deleteById(id);
		return "redirect:/horariosClase";
	}

	@GetMapping("/dia")
	public String verDia(@RequestParam(required = true) String diaSemana, Model model,
						 @AuthenticationPrincipal Usuario usuario) {
		List<HorarioClase> horarios = horarioClaseRepository.findByDiaSemanaAndUsuarioId(diaSemana, usuario.getId());
		horarios.sort(Comparator.comparing(HorarioClase::getHoraInicio, Comparator.nullsLast(Comparator.naturalOrder())));
		model.addAttribute("horariosClase", horarios);
		model.addAttribute("fecha", diaSemana);
		return "horariosClase/HorarioClaseDayView";
	}

	private void cargarListasFormulario(Model model, Usuario usuario) {
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		model.addAttribute("profesores", profesorRepository.findByUsuarioId(usuario.getId()));
	}

	private boolean puedeGestionar(HorarioClase horarioClase, Usuario usuario) {
		return horarioClase.getId() != null && horarioClase.getUsuario().getId().equals(usuario.getId());
	}

	private void resolverAsignatura(HorarioClase horarioClase, Usuario usuario) {
		if (horarioClase.getAsignatura() != null && horarioClase.getAsignatura().getId() != null) {
			Asignatura asignatura = asignaturaRepository.findById(horarioClase.getAsignatura().getId())
				.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
				.orElse(null);
			horarioClase.setAsignatura(asignatura);
		} else {
			horarioClase.setAsignatura(null);
		}
	}
	
	private void resolverProfesor(HorarioClase horarioClase, Usuario usuario) {
		if (horarioClase.getProfesor() != null && horarioClase.getProfesor().getId() != null) {
			Profesor profesor = profesorRepository.findById(horarioClase.getProfesor().getId())
				.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
				.orElse(null);
			horarioClase.setProfesor(profesor);
		} else {
			horarioClase.setProfesor(null);
		}
	}

}
