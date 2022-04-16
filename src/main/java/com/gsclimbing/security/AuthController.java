package com.gsclimbing.security;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.commons.PasswordGenerator;
import com.gsclimbing.commons.ResponseMessage;
import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.service.UserService;
import com.gsclimbing.email.SendEmail;

@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

@RestController
@RequestMapping("auth/")
public class AuthController {

	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/basicauth")
	public AuthenticationBean setAuthentication() {
		return new AuthenticationBean("You are authenticated");
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/role")
	public ResponseEntity<?> getRole() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		GrantedAuthority roleStr = null;
		Optional<? extends GrantedAuthority> role = authentication.getAuthorities().stream().findFirst();
		if (role.isPresent()) {
			roleStr = role.get();
		}
		return ResponseEntity.status(HttpStatus.OK).body(ResponseMessage.builder().message(roleStr.getAuthority()).build());
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/forgetpassword/{email}")
	public ResponseEntity<?> forgetpassword(final @PathVariable String email) {
		String message = null;
		User user = userService.getUserByEmail(email);
		if (user != null) {
			String password = PasswordGenerator.generateCommonLangPassword();
			String subject = "New password";
			message = "Your new password is " + password;
			byte[] bytes = null;
			SendEmail runnable = new SendEmail(user.getEmail(), subject, message, null, null);
			Thread t = new Thread(runnable);
			t.start();
			user.setPassword(password);
			userService.updateUser(user);
			return null;
		} else {
			message = "This email is not on our database;";
			return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
}
