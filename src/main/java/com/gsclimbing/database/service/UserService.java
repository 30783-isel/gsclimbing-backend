package com.gsclimbing.database.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public Optional<User> getUserById(Integer id) {
		return userRepository.findById(id);
	}
	
	public User getUser(String username) {
		return userRepository.findByUsername(username).orElse(null);
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

	public void deleteUser(String userName) {
		Optional<User> user = userRepository.findByUsername(userName);
		userRepository.deleteById(user.get().getIdUser());
	}

	public void updateUser(User user) {
		userRepository.save(user);
	}

}
