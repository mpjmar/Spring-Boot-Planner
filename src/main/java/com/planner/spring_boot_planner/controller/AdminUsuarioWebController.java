package com.planner.spring_boot_planner.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.planner.spring_boot_planner.entity.Rol;
import com.planner.spring_boot_planner.entity.Usuario;
import com.planner.spring_boot_planner.repository.UsuarioRepository;
import com.planner.spring_boot_planner.service.RolRegistroService;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioWebController {

	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;
	private final RolRegistroService rolRegistroService;

	public AdminUsuarioWebController(UsuarioRepository usuarioRepository,
									 PasswordEncoder passwordEncoder,
									 RolRegistroService rolRegistroService) {
		this.usuarioRepository = usuarioRepository;
		this.passwordEncoder = passwordEncoder;
		this.rolRegistroService = rolRegistroService;
	}

	@GetMapping
	public String listar(Model model) {
		model.addAttribute("usuarios", usuarioRepository.findAll());
		return "usuarios/UsuarioListingView";
	}

	@GetMapping("/{id}/editar")
	public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model) {
		Usuario usuario = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		usuario.setPassword("");
		usuario.setRepitePassword("");
		model.addAttribute("usuario", usuario);
		model.addAttribute("accion", "Editar");
		model.addAttribute("adminEdicion", true);
		return "usuarios/UsuarioFormView";
	}

	@PostMapping("/{id}/editar")
	public String guardarEdicion(@PathVariable("id") Long id,
								   @ModelAttribute("usuario") Usuario datosFormulario,
								   Model model) {
		Usuario existente = usuarioRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("El usuario no existe: " + id));
		Rol rolInmutable = existente.getRol() != null ? existente.getRol() : Rol.USER;

		if (datosFormulario.getNombre() == null || datosFormulario.getNombre().isBlank()
			|| datosFormulario.getApellidos() == null || datosFormulario.getApellidos().isBlank()
			|| datosFormulario.getEmail() == null || datosFormulario.getEmail().isBlank()) {
			model.addAttribute("accion", "Editar");
			model.addAttribute("adminEdicion", true);
			model.addAttribute("error", "Los campos de nombre, apellidos y email son obligatorios.");
			model.addAttribute("usuario", datosFormulario);
			return "usuarios/UsuarioFormView";
		}
		existente.setNombre(datosFormulario.getNombre());
		existente.setApellidos(datosFormulario.getApellidos());
		existente.setEmail(datosFormulario.getEmail());
		if (datosFormulario.getPassword() != null && !datosFormulario.getPassword().isBlank()) {
			if (!datosFormulario.getPassword().equals(datosFormulario.getRepitePassword())) {
				model.addAttribute("accion", "Editar");
				model.addAttribute("adminEdicion", true);
				model.addAttribute("error", "Las contraseñas no coinciden");
				datosFormulario.setPassword("");
				datosFormulario.setRepitePassword("");
				model.addAttribute("usuario", datosFormulario);
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
		return "redirect:/admin/usuarios";
	}

	@PostMapping("/{id}/eliminar")
	public String eliminar(@PathVariable("id") Long id,
						   @AuthenticationPrincipal Usuario admin,
						   RedirectAttributes redirectAttributes) {
		if (admin != null && admin.getId().equals(id)) {
			redirectAttributes.addFlashAttribute("error", "No puedes eliminar tu propia cuenta.");
			return "redirect:/admin/usuarios";
		}
		usuarioRepository.deleteById(id);
		redirectAttributes.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
		return "redirect:/admin/usuarios";
	}
}
