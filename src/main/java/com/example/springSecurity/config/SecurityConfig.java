package com.example.springSecurity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import lombok.RequiredArgsConstructor;

import static com.example.springSecurity.entity.Permissions.ADMIN_READ;
import static com.example.springSecurity.entity.Permissions.ADMIN_CREATE;
import static com.example.springSecurity.entity.Permissions.ADMIN_UPDATE;
import static com.example.springSecurity.entity.Permissions.ADMIN_DELETE;
import static com.example.springSecurity.entity.Role.ADMIN;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
	
	private static final String[] WHITE_LIST_URL = {
			"/api/view1/auth/**","/api/view1/demo/**"
	};
	
	private final JwtAuthenticationFilter jwtAuthFilter;
	
	private final AuthenticationProvider authenticationProvider;
	
	private final LogoutHandler logoutHandler;
	

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//		http
//			.httpBasic()
//			.and().csrf()
//			.disable()
//			.authorizeHttpRequests()
//			.requestMatchers("/api/view1/auth/**")
//			.permitAll()
//			.anyRequest()
//			.authenticated()
//			.and()
//			.sessionManagement()
//			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			.and()
//			.authenticationProvider(authenticationProvider)
//			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http
			.csrf(AbstractHttpConfigurer::disable)
			.authorizeHttpRequests(req -> 
					req.requestMatchers(WHITE_LIST_URL)
					.permitAll()
					.requestMatchers("/api/view/admin/**").hasAnyRole(ADMIN.name())
					.requestMatchers(GET,"/api/view/admin/**").hasAnyAuthority(ADMIN_READ.name())
					.requestMatchers(POST,"/api/view/admin/**").hasAnyAuthority(ADMIN_CREATE.name())
					.requestMatchers(PUT,"/api/view/admin/**").hasAnyAuthority(ADMIN_UPDATE.name())
					.requestMatchers(DELETE,"/api/view/admin/**").hasAnyAuthority(ADMIN_DELETE.name())
					.anyRequest()
					.authenticated()
					)
			.sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
			.authenticationProvider(authenticationProvider)
			.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
			.logout(logout -> logout.logoutUrl("/api/v1/auth/logout")
					.addLogoutHandler(logoutHandler)
					.logoutSuccessHandler((request,response,authentication) -> SecurityContextHolder.clearContext())
					)
			;
			
		return http.build();
	}
}