package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ResponseMessages;
import fr.matthieu.chatop.dto.AuthenticationDTO;
import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.dto.UserDTO;
import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.model.UserEntity;
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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
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
	public ResponseEntity<Map<String, String>> login(@RequestBody AuthenticationDTO authenticationDTO) {
		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationDTO.email(), authenticationDTO.password())
			);
			if (authentication.isAuthenticated()) {
				UserEntity userEntity = (UserEntity) authentication.getPrincipal();
				userService.incrementTokenVersion(userEntity);
				String token = jwtService.generate(userEntity);
				return ResponseEntity.ok().body(Map.of("token", token));
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", ResponseMessages.INVALID_CREDENTIALS));
			}
		} catch (InternalAuthenticationServiceException e) {
			log.warn("Internal authentication failed - {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ResponseMessages.SERVICE_UNAVAILABLE));
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
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterDTO registerDTO) {
		try {
			String token = userService.register(registerDTO);

			if(token != null) {
				return  ResponseEntity.ok(Map.of("token", token));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ResponseMessages.REGISTER_FAILED));
			}

		} catch (DataAccessResourceFailureException e) {
		log.warn("DataAccessResourceFailure Exception : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ResponseMessages.SERVICE_UNAVAILABLE));
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
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Successful operation. Returns the authenticated user's information.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized. Invalid or missing JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			}
	)
	public ResponseEntity<UserDTO> getUser() {
		return ResponseEntity.ok().body(userService.getUserResponseDTO());
	}
}