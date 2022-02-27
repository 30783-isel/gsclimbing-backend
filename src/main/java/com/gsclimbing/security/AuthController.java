package com.gsclimbing.security;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gsclimbing.commons.ResponseMessage;

@CrossOrigin(origins = "*", methods = {RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})

@RestController
@RequestMapping("auth/")
public class AuthController {

	Logger log = LoggerFactory.getLogger(getClass());
	
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
	
}
