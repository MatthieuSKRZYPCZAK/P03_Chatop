package fr.matthieu.chatop.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

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
public record ResponseUserDTO(
		Long id,
		String name,
		String email,

		@JsonFormat(pattern = "yyyy/MM/dd")
		String created_at,

		@JsonFormat(pattern = "yyyy/MM/dd")
		String updated_at
) {
}
