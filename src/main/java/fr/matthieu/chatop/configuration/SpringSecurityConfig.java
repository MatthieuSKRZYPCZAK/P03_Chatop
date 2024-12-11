package fr.matthieu.chatop.configuration;

import fr.matthieu.chatop.filter.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import static fr.matthieu.chatop.common.ApiRoutes.*;

/**
 * Configuration class for setting up Spring Security.
 * This class defines the security settings for the application, including authentication, authorization,
 * and session management.
 */
@Configuration
public class SpringSecurityConfig {


	private final BCryptPasswordEncoder passwordEncoder;
	private final JwtAuthFilter jwtAuthFilter;

	public SpringSecurityConfig(BCryptPasswordEncoder passwordEncoder, JwtAuthFilter jwtAuthFilter) {
		this.passwordEncoder = passwordEncoder;
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								REGISTER_URL,
								LOGIN_URL,
								API_DOCS_URL,
								SWAGGER_UI_URL
								).permitAll()
						.anyRequest().authenticated());
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	/**
	 * Provides the {@link AuthenticationManager} bean.
	 * The {@code AuthenticationManager} is used to process authentication requests.
	 *
	 * @param authenticationConfiguration The {@link AuthenticationConfiguration} for setting up the manager.
	 * @return A configured {@link AuthenticationManager}.
	 * @throws Exception If an error occurs while setting up the authentication manager.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	/**
	 * Provides the {@link AuthenticationProvider} bean.
	 * The {@code AuthenticationProvider} is responsible for authenticating users by retrieving user details
	 * and verifying their credentials using the password encoder.
	 *
	 * @param userDetailsService The service for loading user details.
	 * @return A configured {@link AuthenticationProvider}.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
		DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
		daoAuthenticationProvider.setUserDetailsService(userDetailsService);
		daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
		return daoAuthenticationProvider;
	}
}
