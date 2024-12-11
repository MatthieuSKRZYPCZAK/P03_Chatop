package fr.matthieu.chatop.dto;

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
public record RegisterDTO(

		@NotBlank(message = "Email is required")
		@Email(message = "The email address must be valid")
		String email,

		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 128, message = "Password must be between {min} and {max} characters")
		String password,

		@NotBlank(message = "name is required")
		@Size(min = 3, max = 20, message = "Name must be between {min} and {max} characters")
		String name
) {}
