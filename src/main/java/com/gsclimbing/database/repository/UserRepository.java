package com.gsclimbing.database.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gsclimbing.database.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String userName);
	
}
