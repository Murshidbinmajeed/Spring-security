package com.example.springSecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/view1/demo")
public class DemoController {
	
	@GetMapping()
	public ResponseEntity<String> Demo() {
		return ResponseEntity.ok("Hello This Demo Controller!!");
	}

}
