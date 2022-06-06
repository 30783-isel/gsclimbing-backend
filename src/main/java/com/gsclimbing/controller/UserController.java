package com.gsclimbing.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

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
import com.gsclimbing.database.entity.QProject;
import com.gsclimbing.database.entity.QUser;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.ProjectService;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.dto.FilterUserDTO;
import com.gsclimbing.email.SendEmail;
import com.querydsl.jpa.impl.JPAQuery;

@CrossOrigin(origins = "*", methods = { RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE })
@RestController
@RequestMapping(path = "/api/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private ProjectService projectService;
	
	@Autowired
	private EntityManager entityManager;

	@DeleteMapping(value = "/delete/{username}")
	public void deleteUser(@PathVariable String username) {
		User user = userService.getUserByUsername(username);
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
		return userService.getUserByUsername(username);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<?> createUsers(@RequestBody User user) {
		String message = null;
		if (ObjectUtils.isEmpty(user.getUsername()) || ObjectUtils.isEmpty(user.getEmail()) || ObjectUtils.isEmpty(user.getRoles())) {
			message = "Fill all fields";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if (userService.getUserByUsername(user.getUsername()) == null) {
			if (userService.getUserByEmail(user.getEmail()) == null) {
				user.setActive("true");
				String password = PasswordGenerator.generateCommonLangPassword();

				String subject = "Welcome " + user.getName();
				message = "<h2>Welcome to GS-Climbing team.</h2>";
				message+= "<p><span>Credentials</span></p>";
				message+= "<span><b>Username - </b>" + user.getUsername() +"</span>";
				message+= "<p><span><b>Password - </b>" + password +"<span></p>";
				message+= "<p><a href=\"http://31.171.250.208/\">Go to portal</a></p>";
				byte[] bytes = null;
				SendEmail runnable = new SendEmail(user.getEmail(), subject, message, "Defects Inspection Report.pdf", bytes);
				Thread t = new Thread(runnable);
				t.start();

				user.setPassword(password);
				userService.createUser(user);
				return new ResponseEntity<>(HttpStatus.OK);
			} else {
				message = "There is already a user with this email";
				return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {
			message = "There is already a user with this username";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping(value = "/update")
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
				listaProjectos.stream().forEach( project -> updateProjectUser( user.get(),  project));
			}
		}
	}
	
	private Project updateProjectUser(User user, Project project) {
		project.getUsers().add(user);
		return projectService.updateProject(project);
	}
	
	@PostMapping(value = "/search")
	public List<QProject> searchUsers(@RequestBody FilterUserDTO filter) {
		QUser user = QUser.user;
		JPAQuery<QProject> query = new JPAQuery<>(entityManager);
		if(filter.getName() != null) {
			query.from(user).where(user.name.contains(filter.getName()));
		}
		if(filter.getUsername() != null) {
			query.from(user).where(user.username.contains(filter.getUsername()));
		}
		if(filter.getEmail() != null) {
			query.from(user).where(user.email.contains(filter.getEmail()));
		}
		if(filter.getRoles() != null) {
			query.from(user).where(user.roles.contains(filter.getRoles()));
		}
		List<QProject> lista = query.fetch();
		
		return lista;
	}

}
