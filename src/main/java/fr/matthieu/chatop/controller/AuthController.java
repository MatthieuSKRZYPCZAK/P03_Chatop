package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ResponseMessages;
import fr.matthieu.chatop.dto.AuthenticationDTO;
import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.dto.ResponseUserDTO;
import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.service.JWTService;
import fr.matthieu.chatop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static fr.matthieu.chatop.common.ApiRoutes.*;

/**
 * REST Controller responsible for authentication and user-related actions.
 * It includes endpoints for user login, registration, and fetching authenticated user information.
 */
@Slf4j
@RestController
public class AuthController {

	public JWTService jwtService;
    private final UserService userService;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public AuthController(UserService userService, AuthenticationManager authenticationManager, JWTService jwtService) {
		this.jwtService = jwtService;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Logs in a user by validating credentials and generating a JWT token.
	 *
	 * @param authenticationDTO A DTO containing the user's username and password.
	 * @return A {@link ResponseEntity} containing the JWT token if the credentials are valid,
	 *         or an error response with status 401 if authentication fails.
	 */
	@PostMapping(LOGIN_URL)
	public ResponseEntity<Object> login(@RequestBody AuthenticationDTO authenticationDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
		);
		log.info("result {}", authentication.isAuthenticated());
		if (authentication.isAuthenticated()) {
			User user = (User) authentication.getPrincipal();
			userService.incrementTokenVersion(user);
			String token = jwtService.generate(user);
			return ResponseEntity.ok().body(Map.of("token", token));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ResponseMessages.INVALID_CREDENTIALS));
		}
	}

	/**
	 * Registers a new user and generates a JWT token for the newly registered account.
	 *
	 * @param registerDTO A DTO containing user registration details such as username, password, and email.
	 * @return A {@link ResponseEntity} containing the JWT token if registration is successful,
	 *         or an error response with status 400 if registration fails.
	 */
	@PostMapping(REGISTER_URL)
	public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
		log.info("Registering new user");
		String token = userService.register(registerDTO);

		if(token != null) {
			return  ResponseEntity.ok(Map.of("token", token));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ResponseMessages.REGISTER_FAILED));
		}
	}

	/**
	 * Retrieves the currently authenticated user's information.
	 *
	 * @return A {@link ResponseEntity} containing the user's information if authenticated,
	 *         or an error response with status 400 if the user is not authenticated.
	 */
	@GetMapping(ME_URL)
	public ResponseEntity<Object> getUser() {
		User user = userService.getAuthenticateUser();
		if(user != null) {
			ResponseUserDTO userDTO = userService.getUserResponseDTO();
			return ResponseEntity.ok().body(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ResponseMessages.ACCESS_DENIED));
		}
	}
}