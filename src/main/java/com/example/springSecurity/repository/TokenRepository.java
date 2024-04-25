package com.example.springSecurity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.springSecurity.entity.Token;
import com.example.springSecurity.entity.Users;

@EnableJpaRepositories
public interface TokenRepository extends JpaRepository<Token, Long> {
	
	List<Token> findAllValidTokenByUsers(Users id);
//	List<Token> findAllByUsersUser_idAndExpiredFalse(Long userId);
	
	Optional<Token> findByToken(String Token);
}
