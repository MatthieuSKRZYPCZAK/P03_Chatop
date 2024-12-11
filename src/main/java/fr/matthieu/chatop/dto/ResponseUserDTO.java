package fr.matthieu.chatop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Data Transfer Object (DTO) representing the response for user details.
 * This class is used to structure the response sent to the client when user information is retrieved.
 *
 * @param id        The unique identifier of the user.
 * @param name      The name of the user.
 * @param email     The email address of the user.
 * @param created_at The date when the user was created, formatted as "yyyy/MM/dd".
 * @param updated_at The date when the user was last updated, formatted as "yyyy/MM/dd".
 */
@Schema(name = "User details",description = "Represents the user details returned in the API response")
public record ResponseUserDTO(

		@Schema(description = "The unique identifier of the user", example = "123")
		Long id,

		@Schema(description = "The name of the user", example = "John Doe")
		String name,

		@Schema(description = "The email address of the user", example = "john.doe@example.com")
		String email,

		@Schema(description = "The date when the user was created", example = "2023/01/01")
		@JsonFormat(pattern = "yyyy/MM/dd")
		String created_at,

		@Schema(description = "The date when the user was last updated", example = "2023/06/15")
		@JsonFormat(pattern = "yyyy/MM/dd")
		String updated_at
) {
}
