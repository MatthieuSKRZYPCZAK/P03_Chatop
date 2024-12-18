package fr.matthieu.chatop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO representing the data required to create a message.
 */
@Schema(name = "Message details", description = "Represents the data required to create a message.")
public record CreateMessageDTO(

		@Schema(description = "The content of the message", example = "Hello, I am interested in your rental.")
		@NotBlank(message = "Message content is required.")
		@Size(min = 1, max = 500, message = "The message must be between {min} and {max} characters.")
		String message,

		@Schema(description = "The ID of the user sending the message", example = "1")
		@NotNull(message = "User ID is required.")
		Long user_id,

		@Schema(description = "The ID of the rental the message is about", example = "10")
		@NotNull(message = "Rental ID is required.")
		Long rental_id
) {
}
