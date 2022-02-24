package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.commons.ResponseMessage;
import com.gsclimbing.database.entity.Project;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.UserService;


@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private ProjectService projectService;
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/delete/{username}")
	public void deleteUser(@PathVariable String username) {
		Optional<User> user = userService.getUser(username);
		user.get().setProjects(null);
		userService.deleteUser(username);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/users")
	public List<User> getUsers() {
		return userService.getAllUsers();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/user/{username}")
	public User getUser(@PathVariable String username) {
		return userService.getUser(username).get();
	}

	@RequestMapping(method = RequestMethod.POST, value = "/create")
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		
		String message = null;
		if(StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())|| StringUtils.isEmpty(user.getRoles())) {
			message = "Fill all fields";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (!userService.getUser(user.getUsername()).isPresent()) {
			user.setActive("true");
			userService.createUser(user);
			message = "User created";
			return null;
		}else {
			message = "There is already a user with this username";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/user/{id}")
	public void updateUser(@PathVariable Integer id, @RequestBody User user) {
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
			if(!StringUtils.isEmpty(listProjects)) {
				List<Project> listaProjectos = Arrays.asList(listProjects.split("-")).stream().map(id -> projectService.getProjectById(Integer.valueOf(id)).orElse(null)).collect(Collectors.toList());
				user.get().setProjects(new HashSet<>(listaProjectos));
			}else {
				user.get().setProjects(null);
			}
			userService.updateUser(user.get());
		}
	}
	
}
