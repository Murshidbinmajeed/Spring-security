package com.example.springSecurity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.springSecurity.entity.Users;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<Users, Long>{
	
	Optional<Users> findByEmail(String email);
}
