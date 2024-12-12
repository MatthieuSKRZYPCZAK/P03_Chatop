package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ResponseMessages;
import fr.matthieu.chatop.dto.AuthenticationDTO;
import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.dto.ResponseUserDTO;
import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.service.JWTService;
import fr.matthieu.chatop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Authentication", description = "Endpoints for user authentication and management")
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
	@Operation(
			summary = "User login",
			description = "Authenticates a user with their credentials and returns a JWT token if the login is successful.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successful authentication. Returns a JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthenticationDTO.class))
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized. Invalid username or password.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			}
	)
	public ResponseEntity<Object> login(@RequestBody AuthenticationDTO authenticationDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password())
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
	@Operation(
			summary = "User registration",
			description = "Registers a new user and returns a JWT token for the newly created account.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successful registration. Returns a JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = RegisterDTO.class))
					),
					@ApiResponse(
							responseCode = "400",
							description = "Bad Request. Registration failed due to invalid data.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			}
	)
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
	 *         or an error response with status 401 if the user is not authenticated.
	 */
	@GetMapping(ME_URL)
	@Operation(
			summary = "Retrieve authenticated user information",
			description = "Fetches the details of the authenticated user. Requires a valid JWT token.",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successful operation. Returns the authenticated user's information.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseUserDTO.class))
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized. Invalid or missing JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			},
			security = @SecurityRequirement(name = "bearerAuth")
	)
	public ResponseEntity<Object> getUser() {
		User user = userService.getAuthenticateUser();
		if(user != null) {
			ResponseUserDTO userDTO = userService.getUserResponseDTO();
			return ResponseEntity.ok().body(userDTO);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ResponseMessages.UNAUTHORIZED_ACCESS));
		}
	}
}