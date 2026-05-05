package com.planner.spring_boot_planner.controller;

import java.time.LocalTime;
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

import com.planner.spring_boot_planner.DiaLectivo;
import com.planner.spring_boot_planner.dto.HorarioClaseFormDTO;
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
	public String mostrarFormularioNuevo(@RequestParam(required = false) DiaLectivo diaLectivo,
                                     	@AuthenticationPrincipal Usuario usuario,
                                     	Model model) {
		HorarioClaseFormDTO dto = new HorarioClaseFormDTO();

		if (diaLectivo != null)
			dto.setDiaLectivo(diaLectivo);

		model.addAttribute("horarioClase", dto);
		cargarHorasFormulario(model);
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Añadir");

		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("horarioClase") HorarioClaseFormDTO dto,
							   BindingResult result, Model model, 
							   @AuthenticationPrincipal Usuario usuario) {
		if (result.hasErrors()) {
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}

		LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
		LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

		if (!horaInicio.isBefore(horaFin)) {
			result.reject("horario.rango", "La hora de inicio debe ser anterior a la hora de fin");
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}

		if (horarioClaseRepository.existeSolapamiento(
				usuario.getId(),
				dto.getDiaLectivo(),
				horaInicio,
				horaFin)) {
			result.reject("horario.solapado", "Ya existe un horario en ese tramo");
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Añadir");
			return "horariosClase/HorarioClaseFormView";
		}
		
		HorarioClase horarioClase = new HorarioClase();

		horarioClase.setDiaLectivo(dto.getDiaLectivo());
		horarioClase.setHoraInicio(horaInicio);
		horarioClase.setHoraFin(horaFin);
		horarioClase.setUsuario(usuario);
		horarioClase.setAsignatura(resolverAsignatura(dto.getAsignaturaId(), usuario));
		horarioClaseRepository.save(horarioClase);
		
		return "redirect:/horariosClase/dia/" + horarioClase.getDiaLectivo();
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, 
										  @AuthenticationPrincipal Usuario usuario) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("Bloque de horarioClase no encontrado: " + id));
		if (!puedeGestionar(horarioClase, usuario))
			return "redirect:/horariosClase";

		HorarioClaseFormDTO dto = new HorarioClaseFormDTO();
		dto.setId(horarioClase.getId());
		dto.setDiaLectivo(horarioClase.getDiaLectivo());
		dto.setHoraInicio(horarioClase.getHoraInicio().toString());
		dto.setHoraFin(horarioClase.getHoraFin().toString());

		if (horarioClase.getAsignatura() != null) 
			dto.setAsignaturaId(horarioClase.getAsignatura().getId());
		
		model.addAttribute("horarioClase", dto);
		cargarHorasFormulario(model);
		cargarListasFormulario(model, usuario);
		model.addAttribute("accion", "Editar");
		return "horariosClase/HorarioClaseFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(
			@PathVariable("id") Long id,
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
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}

		LocalTime horaInicio = LocalTime.parse(dto.getHoraInicio());
		LocalTime horaFin = LocalTime.parse(dto.getHoraFin());

		if (!horaInicio.isBefore(horaFin)) {
			result.reject("horario.rango", "La hora de inicio debe ser anterior a la hora de fin");
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}

		if (horarioClaseRepository.existeSolapamientoEditando(
				id,
				usuario.getId(),
				dto.getDiaLectivo(),
				horaInicio,
				horaFin)) {
			result.reject("horario.solapado", "Ya existe un horario en ese tramo");
			cargarHorasFormulario(model);
			cargarListasFormulario(model, usuario);
			model.addAttribute("accion", "Editar");
			return "horariosClase/HorarioClaseFormView";
		}

		Asignatura asignatura = asignaturaRepository.findById(dto.getAsignaturaId())
			.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
			.orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));

		existente.setDiaLectivo(dto.getDiaLectivo());
		existente.setHoraInicio(horaInicio);
		existente.setHoraFin(horaFin);
		existente.setAsignatura(asignatura);
		existente.setUsuario(usuario);

		horarioClaseRepository.save(existente);

		return "redirect:/horariosClase/dia/" + existente.getDiaLectivo();
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id, 
							@AuthenticationPrincipal Usuario usuario) {
		HorarioClase horarioClase = horarioClaseRepository.findById(id).orElse(null);
		if (horarioClase == null || !puedeGestionar(horarioClase, usuario))
			return "redirect:/horariosClase";
		DiaLectivo diaLectivo = horarioClase.getDiaLectivo();
		horarioClaseRepository.deleteById(id);
		return "redirect:/horariosClase/dia/" + diaLectivo;
	}

	@GetMapping("/dia/{diaLectivo}")
	public String verDia(@PathVariable("diaLectivo") String diaLectivo, Model model,
						@AuthenticationPrincipal Usuario usuario) {
		DiaLectivo dia = DiaLectivo.valueOf(diaLectivo.toUpperCase());
		List<HorarioClase> horarios = horarioClaseRepository.findByDiaLectivoAndUsuarioId(dia, usuario.getId());
		
		horarios.sort(Comparator.comparing(HorarioClase::getHoraInicio, 
						Comparator.nullsLast(Comparator.naturalOrder()))
						.thenComparing(HorarioClase::getHoraFin,
						Comparator.nullsLast(Comparator.naturalOrder())));
		
		model.addAttribute("horariosClase", horarios);
		model.addAttribute("diaLectivo", dia);
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
		
		model.addAttribute("horasInicioDisponibles", horasPosibles());
		model.addAttribute("horasFinDisponibles", horasPosibles());
		
		return "horariosClase/HorarioClaseDayView";
	}

	private void cargarListasFormulario(Model model, Usuario usuario) {
		model.addAttribute("asignaturas", asignaturaRepository.findByUsuarioId(usuario.getId()));
	}

	private void cargarHorasFormulario(Model model) {
		List<String> horas = horasPosibles();
		model.addAttribute("horasDisponibles", horas.subList(0, horas.size() - 1));
		model.addAttribute("horasFinDisponibles", horas);
	}

	private boolean puedeGestionar(HorarioClase horarioClase, Usuario usuario) {
		return horarioClase.getId() != null && horarioClase.getUsuario().getId().equals(usuario.getId());
	}

	private Asignatura resolverAsignatura(Long asignaturaId, Usuario usuario) {
		return asignaturaRepository.findById(asignaturaId)
			.filter(a -> a.getUsuario() != null && a.getUsuario().getId().equals(usuario.getId()))
			.orElseThrow(() -> new RuntimeException("Asignatura no encontrada"));
	}

	@ModelAttribute("diasLectivos")
	public List<DiaLectivo> diasLectivos() {
		return List.of(DiaLectivo.values());
	}

	private List<String> horasPosibles() {
		return List.of(
			"08:15", "09:15", "10:15", "11:15",
			"11:45", "12:45", "13:45", "14:45"
		);
	}

}
