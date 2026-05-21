package com.planner.spring_boot_planner.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.planner.spring_boot_planner.entity.Rol;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.UsuarioRepository;
import com.planner.spring_boot_planner.service.RolRegistroService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioWebController {
	
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final RolRegistroService rolRegistroService;

	public UsuarioWebController(UsuarioRepository usuarioRepository,
								PasswordEncoder passwordEncoder,
								RolRegistroService rolRegistroService) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.rolRegistroService = rolRegistroService;
	}

	@GetMapping("/perfil")
	public String verPerfil(Model model, @AuthenticationPrincipal Usuario usuario) {
		model.addAttribute("usuario", usuario);
		return "usuarios/UsuarioPerfilView";
	}

	@GetMapping("/nuevo")
	public String mostrarFormularioNuevo(Model model, @AuthenticationPrincipal Usuario principal) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("accion", "Añadir");
		model.addAttribute("adminEdicion", false);
		setCancelarRegistro(model, principal);
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/nuevo")
	public String guardarNuevo(@Valid @ModelAttribute("usuario") Usuario usuario,
								BindingResult result, Model model,
								@AuthenticationPrincipal Usuario principal) {
		if (result.hasErrors()) {
			model.addAttribute("accion", "Añadir");
			model.addAttribute("adminEdicion", false);
			setCancelarRegistro(model, principal);
			return "usuarios/UsuarioFormView";
		}
		if (!usuario.getPassword().equals(usuario.getRepitePassword())) {
			model.addAttribute("accion", "Añadir");
			model.addAttribute("adminEdicion", false);
			setCancelarRegistro(model, principal);
			model.addAttribute("error", "Las contraseñas no coinciden.");
			return "usuarios/UsuarioFormView";
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		rolRegistroService.aplicarRolAlRegistro(usuario);
		usuarioRepository.save(usuario);
		return "redirect:/login";
	}

	private static void setCancelarRegistro(Model model, Usuario principal) {
		if (principal == null) {
			model.addAttribute("cancelarRegistro", "LOGIN");
		} else if (principal.esAdmin()) {
			model.addAttribute("cancelarRegistro", "ADMIN");
		} else {
			model.addAttribute("cancelarRegistro", "DASHBOARD");
		}
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model,
										  @AuthenticationPrincipal Usuario autenticado) {
		if (autenticado == null || !autenticado.getId().equals(id))
			return "redirect:/usuario/perfil";
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		usuario.setPassword("");
		usuario.setRepitePassword("");
		model.addAttribute("usuario", usuario);
		model.addAttribute("accion", "Editar");
		model.addAttribute("adminEdicion", false);
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEleccion(@PathVariable("id") Long id,
								@ModelAttribute("usuario") Usuario datosFormulario,
								Model model,
								@AuthenticationPrincipal Usuario autenticado) {
		if (autenticado == null || !autenticado.getId().equals(id))
			return "redirect:/usuario/perfil";
		Usuario existente = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		Rol rolInmutable = existente.getRol() != null ? existente.getRol() : Rol.USER;
		if (datosFormulario.getNombre() == null || datosFormulario.getNombre().isBlank()
			|| datosFormulario.getApellidos() == null || datosFormulario.getApellidos().isBlank()
			|| datosFormulario.getEmail() == null || datosFormulario.getEmail().isBlank()) {
				model.addAttribute("accion", "Editar");
				model.addAttribute("error", "Los campos de nombre, apellidos y email son obligatorios.");
				model.addAttribute("usuario", datosFormulario);
				model.addAttribute("adminEdicion", false);
				return "usuarios/UsuarioFormView";
		}
		existente.setNombre(datosFormulario.getNombre());
		existente.setApellidos(datosFormulario.getApellidos());
		existente.setEmail(datosFormulario.getEmail());
		if (datosFormulario.getPassword() != null && !datosFormulario.getPassword().isBlank()) {
			if (!datosFormulario.getPassword().equals(datosFormulario.getRepitePassword())) {
				model.addAttribute("accion", "Editar");
				model.addAttribute("error", "Las contraseñas no coinciden");
				datosFormulario.setPassword("");
				datosFormulario.setRepitePassword("");
				model.addAttribute("usuario", datosFormulario);
				model.addAttribute("adminEdicion", false);
				return "usuarios/UsuarioFormView";
			}
			existente.setPassword(passwordEncoder.encode(datosFormulario.getPassword()));
			existente.setRepitePassword(existente.getPassword());
		}
		existente.setRol(rolInmutable);
		if (rolRegistroService.esEmailAdministrador(existente.getEmail())) {
			existente.setRol(Rol.ADMIN);
		}
		usuarioRepository.save(existente);
		return "redirect:/usuario/perfil";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id, 
						   @AuthenticationPrincipal Usuario usuario,
						   Model model) {
		if (usuario == null || !usuario.getId().equals(id))
			return "redirect:/usuario/perfil";
		usuarioRepository.deleteById(id);
		model.addAttribute("mensaje", "Usuario eliminado correctamente");
		return "redirect:/login";
	}
}
