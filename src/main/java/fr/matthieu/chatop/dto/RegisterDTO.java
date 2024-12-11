package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

/**
 * A Data Transfer Object used for user registration.
 * <p>
 * This record ensures that the provided user information is valid before
 * proceeding with the registration process.
 * <ul>
 *   <li>Email must be a valid format and not blank.</li>
 *   <li>Password must not be blank and must adhere to a length constraint.</li>
 *   <li>Name must not be blank and must fall within a specified length range.</li>
 * </ul>
 */
@Schema(name = "Register", description = "Data required for user registration")
public record RegisterDTO(

		@Schema(description = "The user's email address", example = "john.doe@example.com")
		@NotBlank(message = "Email is required")
		@Email(message = "The email address must be valid")
		String email,

		@Schema(description = "The user's password (must be between 8 and 128 characters)", example = "P@ssw0rd")
		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 128, message = "Password must be between {min} and {max} characters")
		String password,

		@Schema(description = "The name chosen by the user", example = "john_doe")
		@NotBlank(message = "name is required")
		@Size(min = 3, max = 20, message = "Name must be between {min} and {max} characters")
		String name
) {}
