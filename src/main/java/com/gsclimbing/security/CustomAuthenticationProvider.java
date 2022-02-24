package com.gsclimbing.security;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.gsclimbing.database.entity.User;
import com.gsclimbing.database.repository.UserRepository;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private UserRepository userService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException, NoSuchElementException {
		Authentication auth = null;
		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		Optional<User> user = userService.findByUsername(username);
		if (password.matches(user.get().getPassword())) {
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(user.get().getRoles()));
			auth = new UsernamePasswordAuthenticationToken(username, password, authorities);
		}
		return auth;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}
}