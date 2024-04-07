package com.gsclimbing.database.service;

import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username).orElse(null);
	}
	
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email).orElse(null);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public void createUser(User user) {
		userRepository.save(user);
	}

	public void deleteUser(Integer id) {
		userRepository.deleteById(id);
	}

	public void deleteUser(User user) {
		userRepository.delete(user);
	}

	public void updateUser(User user) {
		userRepository.save(user);
	}

}
