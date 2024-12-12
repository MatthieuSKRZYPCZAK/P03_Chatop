package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing the authentication credentials.
 * This class is used to encapsulate the username and password provided by the client during login.
 *
 * @param email The email address of the user attempting to log in.
 * @param password The password of the user attempting to log in.
 */
@Schema(name = "Login", description = "Data required for user login")
public record AuthenticationDTO(

		@Schema(description = "The email address of the user", example = "user@example.com")
		String email,

		@Schema(description = "The password of the user", example = "P@ssw0rd")
		String password
) {
}
