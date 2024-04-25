package com.example.springSecurity.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import com.example.springSecurity.config.JwtService;
import com.example.springSecurity.controller.AuthenticationResponse;
import com.example.springSecurity.entity.Token;
import com.example.springSecurity.entity.TokenType;
import com.example.springSecurity.entity.Users;
import com.example.springSecurity.entity.entityVO.AuthenticationRequest;
import com.example.springSecurity.entity.entityVO.RegisterRequest;
import com.example.springSecurity.repository.TokenRepository;
import com.example.springSecurity.repository.UserRepository;
import com.example.springSecurity.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final JwtService jwtService;
	private final AuthenticationManager authManager;
	private final TokenRepository tokenRepository;
	
	@Override
	public AuthenticationResponse register(RegisterRequest request) {
		var user = Users.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.paswrd(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.build();
		var savedUser = userRepository.save(user);
		var jwtToken = jwtService.generateToken(user);
		var refreshtoken = jwtService.generateRefreshToken(user);
		saveUserToken(savedUser, jwtToken);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshtoken)
				.build();
	}

	@Override
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getEmail(),
						request.getPassword()));
		System.out.println(request.getEmail());
		var user = userRepository.findByEmail(request.getEmail())
				.orElseThrow();
		var jwtToken = jwtService.generateToken(user);
		var refreshtoken = jwtService.generateRefreshToken(user);
		revokeAllUserTokens(user);
		saveUserToken(user, jwtToken);
		return AuthenticationResponse.builder()
				.accessToken(jwtToken)
				.refreshToken(refreshtoken)
				.build();
	}

	@Override
	public void saveUserToken(Users user, String jwtToken) {
		var token = Token.builder()
				.users(user)
				.token(jwtToken)
				.tokenType(TokenType.BEARER)
				.revoked(false)
				.expired(false)
				.build();
		tokenRepository.save(token);
	}

	@Override
	public void revokeAllUserTokens(Users users) {
		Users newusers = new Users();
		newusers.setId(users.getId());
		var validUserToken = tokenRepository.findAllValidTokenByUsers(newusers);
		if(validUserToken==null) {
			return;
		}
		validUserToken.forEach(token -> {
			token.setExpired(true);
			token.setRevoked(true);
		});
		tokenRepository.saveAll(validUserToken);
	}
	
	@Override
	public void refreshToken(HttpServletRequest request,
						HttpServletResponse response)
						throws java.io.IOException{
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String refreshToken;
		final String userEmail;
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}
		refreshToken = authHeader.substring(7);
		userEmail = jwtService.extractUsername(refreshToken);
		if(userEmail != null) {
			var user = this.userRepository.findByEmail(userEmail)
					.orElseThrow();
			if(jwtService.isTokenValid(refreshToken, user)) {
				var accesToken = jwtService.generateToken(user);
				revokeAllUserTokens(user);
				saveUserToken(user, refreshToken);
				var authResponse = AuthenticationResponse.builder()
						.accessToken(accesToken)
						.refreshToken(refreshToken)
						.build();
				new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
			}
		}
	}
	

}
