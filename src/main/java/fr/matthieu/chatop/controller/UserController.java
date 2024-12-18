package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.dto.UserDTO;
import fr.matthieu.chatop.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static fr.matthieu.chatop.common.ApiRoutes.USER_ID_URL;

/**
 * REST Controller responsible for handling user-related actions.
 * It includes endpoints for fetching user details by their ID.
 */
@Slf4j
@RestController
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {


	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Retrieves the details of a specific user by their ID.
	 *
	 * @param id The ID of the user to retrieve.
	 * @return A {@link ResponseEntity} containing the user details if found,
	 *         or an error response if the user does not exist or if the request is invalid.
	 */
	@GetMapping(USER_ID_URL)
	@Operation(
			summary = "Get user by ID",
			description = "Fetches the details of a specific user by their ID.",
			security = @SecurityRequirement(name = "bearerAuth"),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Rental created successfully",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class))
					),
					@ApiResponse(
							responseCode = "400",
							description = "Bad Request. Errors include: missing or invalid data",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized. Invalid or missing JWT token.",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
					)
			}
	)
	public ResponseEntity<Object> getUserById(@PathVariable Long id) {
		log.info("Get user by ID: {}", id);
		UserDTO user = userService.getUserDTOById(id);
		return ResponseEntity.ok().body(user);
	}
}