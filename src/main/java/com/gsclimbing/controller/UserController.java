package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.commons.PasswordGenerator;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.email.SendEmail;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;

	@DeleteMapping(value = "/delete/{username}")
	public void deleteUser(@PathVariable String username) {
		User user = userService.getUser(username);
		if (user != null) {
			user.setProjects(null);
			userService.deleteUser(username);
		}
	}

	@GetMapping(value = "/users")
	public List<User> getUsers() {
		return userService.getAllUsers();
	}

	@GetMapping(value = "/user/{username}")
	public User getUser(@PathVariable String username) {
		return userService.getUser(username);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		String message = null;
		if (ObjectUtils.isEmpty(user.getUsername()) || ObjectUtils.isEmpty(user.getEmail()) || ObjectUtils.isEmpty(user.getRoles())) {
			message = "Fill all fields";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (userService.getUser(user.getUsername()) == null) {
			user.setActive("true");
			String password = PasswordGenerator.generateCommonLangPassword();
			
			String subject = "Welcome " + user.getName();
			message = "Your password is: " + password;
			byte[] bytes = null;
			SendEmail runnable = new SendEmail(user.getEmail(), subject, message, "Defects Inspection Report.pdf", bytes);
			Thread t = new Thread(runnable);
			t.start();
			
			user.setPassword(password);
			userService.createUser(user);
			return null;
		} else {
			message = "There is already a user with this username";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping(value = "/user/")
	public void updateUser(@RequestBody User user) {
		userService.updateUser(user);
	}

	@GetMapping(value = "/user-projects/{userId}")
	public List<Project> getUserData(@PathVariable final Integer userId) {
		Optional<User> user = userService.getUserById(userId);
		return user.isPresent() ? new ArrayList<>(user.get().getProjects()) : null;
	}

	@GetMapping(value = "/update-user-projects/{userId}/{projects}")
	public void updateUserData(@PathVariable("userId") final Integer userId, @PathVariable("projects") String listProjects) {
		Optional<User> user = userService.getUserById(userId);
		if (user.isPresent()) {
			if (!ObjectUtils.isEmpty(listProjects)) {
				List<Project> listaProjectos = Arrays.asList(listProjects.split("-")).stream().map(id -> projectService.getProjectById(Integer.valueOf(id))).collect(Collectors.toList());
				user.get().setProjects(new HashSet<>(listaProjectos));
			} else {
				user.get().setProjects(null);
			}
			userService.updateUser(user.get());
		}
	}

}
