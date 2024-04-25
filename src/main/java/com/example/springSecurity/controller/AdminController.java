package com.example.springSecurity.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/view/admin")
public class AdminController {
	
	@GetMapping("/test")
	public String admintest() {
		return "admin test ok";
	}

}
