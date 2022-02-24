package com.gsclimbing.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
    private CustomAuthenticationProvider authProvider;
	
	@Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("janga").password("janga").roles("ADMIN");
		auth.authenticationProvider(authProvider);
    }
	 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.GET,"/auth/**").permitAll()
            .antMatchers(HttpMethod.POST,"/auth/**").permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/auth/**").permitAll()
            .antMatchers(HttpMethod.GET,"/api/**").permitAll()
            .antMatchers(HttpMethod.OPTIONS, "/api/**").permitAll()
            .antMatchers(HttpMethod.POST,"/api/**").permitAll()
            .antMatchers(HttpMethod.PUT,"/api/**").permitAll()
            .antMatchers(HttpMethod.DELETE,"/api/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic();
		
//		http.authorizeRequests().antMatchers("/api/**").permitAll();
	}
	
	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
	
}


