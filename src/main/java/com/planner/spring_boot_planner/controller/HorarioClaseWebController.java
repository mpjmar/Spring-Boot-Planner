package com.planner.spring_boot_planner.controller;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.planner.spring_boot_planner.DiaSemana;
import com.planner.spring_boot_planner.dto.HorarioClaseFormDTO;
import com.planner.spring_boot_planner.dto.HorarioClaseUpdateDTO;
import com.planner.spring_boot_planner.entity.Asignatura;
import com.planner.spring_boot_planner.entity.HorarioClase;
import com.planner.spring_boot_planner.entity.Usuario;
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
	public String listarHorarios(@AuthenticationPrincipal Usuario usuario, Model model) {
		model.addAttribute("horariosClase", horarioClaseRepository.findByUsuarioId(usuario.getId()));
		return "horariosClase/HorarioClaseListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(@RequestParam(required = false) DiaSemana diaSemana,
                                     	@AuthenticationPrincipal Usuario usuario,
                                     	Model model) {
		HorarioClaseFormDTO dto = new HorarioClaseFormDTO();
		if (diaSemana != null)
			dto.setDiaSemana(diaSemana);
		model.addAttribute("horarioClase", dto);
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Añadir");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horarioClase") HorarioClaseFormDTO dto,
							   BindingResult result, Model model, 
							   @AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}
		HorarioClase horarioClase = new HorarioClase();

		horarioClase.setDiaSemana(dto.getDiaSemana());
		horarioClase.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
		horarioClase.setHoraFin(LocalTime.parse(dto.getHoraFin()));
		horarioClase.setUsuario(usuario);
		horarioClase.setAsignatura(resolverAsignatura(dto.getAsignaturaId(), usuario));
		horarioClaseRepository.save(horarioClase);
		return "redirect:/horariosClase/dia/" + horarioClase.getDiaSemana();
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horarioClase no encontrado: " + id));
		if (!puedeGestionar(horarioClase, usuario))
			return "redirect:/horariosClase";

		HorarioClaseFormDTO dto = new HorarioClaseFormDTO();
		dto.setId(horarioClase.getId());
		dto.setDiaSemana(horarioClase.getDiaSemana());
		dto.setHoraInicio(horarioClase.getHoraInicio().toString());
		dto.setHoraFin(horarioClase.getHoraFin().toString());

		if (horarioClase.getAsignatura() != null) 
			dto.setAsignaturaId(horarioClase.getAsignatura().getId());
		
		model.addAttribute("horarioClase", dto);
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Editar");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(
			@PathVariable Long id,
			@Valid @ModelAttribute("horarioClase") HorarioClaseFormDTO dto,
			BindingResult result,
			Model model,
			@AuthenticationPrincipal Usuario usuario) {

		HorarioClase existente = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Horario de clase no encontrado: " + id));

		if (!puedeGestionar(existente, usuario)) {
			return "redirect:/horariosClase";
		}

		if (result.hasErrors()) {
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}

		Asignatura asignatura = asignaturaRepository.findById(dto.getAsignaturaId())
			.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
			.orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

		existente.setDiaSemana(dto.getDiaSemana());
		existente.setHoraInicio(LocalTime.parse(dto.getHoraInicio()));
		existente.setHoraFin(LocalTime.parse(dto.getHoraFin()));
		existente.setAsignatura(asignatura);
		existente.setUsuario(usuario);

		horarioClaseRepository.save(existente);

		return "redirect:/horariosClase/dia/" + existente.getDiaSemana();
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

	@GetMapping("/dia/{diaSemana}")
	public String verDia(@PathVariable String diaSemana, Model model,
						@AuthenticationPrincipal Usuario usuario) {
		DiaSemana dia = DiaSemana.valueOf(diaSemana.toUpperCase());
		List<HorarioClase> horarios = horarioClaseRepository.findByDiaSemanaAndUsuarioId(dia, usuario.getId());
		horarios.sort(Comparator.comparing(HorarioClase::getHoraInicio, 
						Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(HorarioClase::getHoraFin,
						Comparator.nullsLast(Comparator.naturalOrder())));
		model.addAttribute("horariosClase", horarios);
		model.addAttribute("diaSemana", dia);

		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));

		List<String> horasPosibles = List.of(
			"08:15", "09:15", "10:15", "11:15", "11:45", "12:45", "13:45", "14:45"
		);
		model.addAttribute("horasInicioDisponibles", horasPosibles);
		model.addAttribute("horasFinDisponibles", horasPosibles);

		Set<String> horasOcupadas = horarios.stream()
			.map(h -> h.getHoraInicio().toString().substring(0,5))
			.collect(Collectors.toSet());

		List<String> horasDisponibles = horasPosibles.stream()
			.filter(hora -> !horasOcupadas.contains(hora))
			.collect(Collectors.toList());

		model.addAttribute("horasDisponibles", horasDisponibles);
		return "horariosClase/HorarioClaseDayView";
	}

	private void cargarListasFormulario(Model model, Usuario usuario) {
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
	}

	private boolean puedeGestionar(HorarioClase horarioClase, Usuario usuario) {
		return horarioClase.getId() != null && horarioClase.getUsuario().getId().equals(usuario.getId());
	}

	private Asignatura resolverAsignatura(Long asignaturaId, Usuario usuario) {
		return asignaturaRepository.findById(asignaturaId)
			.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
			.orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
	}

	@ModelAttribute("diasSemana")
	public List<DiaSemana> diasSemana() {
		return List.of(DiaSemana.values());
	}

}
