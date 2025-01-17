package com.example.springSecurity.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	
	private static final String SECRET_KEY = "5bf4f5889bc66ed03368a5041938d40c19ea25b501ac77ec20d3cb20f7a199e2";
	
//	@Value("${application.security.jwt.expiration}")
	private long jwtExpiration = 86400000;
	
//	@Value("${application.security.jwt.refresh-token.expiration}")
	private long refreshExpiration = 604800000;

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}
	
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
	
	public String generateToken(UserDetails userDetails) {
		return generateToken(new HashMap<>(), userDetails);
	}
	
	public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return buildtoken(extraClaims, userDetails, jwtExpiration);
	}
	
	public String generateRefreshToken(UserDetails userDetails) {
		return buildtoken(new HashMap<>(), userDetails,refreshExpiration);
	}
	
	private String buildtoken(Map<String, Object> extraClaims,UserDetails userDetails, long expiration) {
		return Jwts
				.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis()+ expiration))
				.signWith(getSignInKey(),SignatureAlgorithm.HS256)
				.compact();
	}
	
	
	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
	
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts
				.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	private Key getSignInKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
