package fr.matthieu.chatop.dto;

import jakarta.validation.constraints.*;

public record RegisterDTO(

		@NotBlank(message = "Email is required")
		@Email(message = "The email address must be valid")
		String email,

		@NotBlank(message = "Password is required")
		@Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters")
		String password,

		@NotBlank(message = "name is required")
		@Size(min = 3, max = 20)
		String name
) {}
