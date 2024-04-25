package com.example.springSecurity.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springSecurity.entity.entityVO.AuthenticationRequest;
import com.example.springSecurity.entity.entityVO.RegisterRequest;
import com.example.springSecurity.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/view1/auth")
@CrossOrigin(origins="http://localhost:5173")
public class AuthController {
	
	private final AuthenticationService authenticationService;
		
	public AuthController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@PostMapping("/resgister")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
		return ResponseEntity.ok(authenticationService.register(request));
	}
	
	@PostMapping("/authenticate")
	public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
		 return authenticationService.authenticate(request);
	 }
	
	@PostMapping("/refresh-token")
	public void refreshtoken(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		authenticationService.refreshToken(request, response);
	}
}
