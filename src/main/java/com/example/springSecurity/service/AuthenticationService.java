package com.example.springSecurity.service;

import com.example.springSecurity.controller.AuthenticationResponse;
import com.example.springSecurity.entity.Users;
import com.example.springSecurity.entity.entityVO.AuthenticationRequest;
import com.example.springSecurity.entity.entityVO.RegisterRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public interface AuthenticationService {

	public AuthenticationResponse register(RegisterRequest request);

	public AuthenticationResponse authenticate(AuthenticationRequest request);
	
	public void saveUserToken(Users user, String jwtToken);
	
	public void revokeAllUserTokens(Users users);
	
	public void refreshToken(HttpServletRequest request,
			HttpServletResponse response)
			throws java.io.IOException;
	
//	public void refreshToken(HttpServletRequest request,HttpServletResponse response);
	
}
