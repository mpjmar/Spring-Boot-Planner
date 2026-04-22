package com.planner.spring_boot_planner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {

	@GetMapping("/login")
	public String mostrarLogin() {
		return "login";
	}
}
