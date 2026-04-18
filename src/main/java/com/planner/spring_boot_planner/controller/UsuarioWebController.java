package com.planner.spring_boot_planner.controller;

import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.entity.Cuadrante;
import com.planner.spring_boot_planner.entity.BloqueEstudio;
import com.planner.spring_boot_planner.repository.UsuarioRepository;
import com.planner.spring_boot_planner.repository.CuadranteRepository;
import com.planner.spring_boot_planner.repository.BloqueEstudioRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/usuario")
public class UsuarioWebController {
	
	private final UsuarioRepository usuarioRepository;
	private final CuadranteRepository cuadranteRepository;
	private final BloqueEstudioRepository bloqueEstudioRepository;

	public UsuarioWebController(UsuarioRepository usuarioRepository,
								CuadranteRepository cuadranteRepository,
								BloqueEstudioRepository bloqueEstudioRepository) {
		this.usuarioRepository = usuarioRepository;
		this.cuadranteRepository = cuadranteRepository;
		this.bloqueEstudioRepository = bloqueEstudioRepository;
	}

	@GetMapping
	public String listarUsuarios(Model model) {
		model.addAttribute("usuarios", usuarioRepository.findAll());
		return "usuarios/usuarioListingView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("accion", "Añadir");
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("usuario") Usuario usuario,
								BindingResult result, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Añadir");
			return "usuarios/UsuarioFormView";
		}
		usuarioRepository.save(usuario);
		return "redirect:/usuarios";
	}
}
