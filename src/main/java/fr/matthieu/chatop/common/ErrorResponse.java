package fr.matthieu.chatop.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A class representing the structure of an error response sent by the API.
 */
@Schema(name = "Error Response", description = "Represents an error response with a descriptive message")
public class ErrorResponse {

	@Schema(description = "The error message describing the problem", example = "An unexpected error occurred.")
	private final String message;

	public ErrorResponse(String message) {
		this.message = message;
	}

	@SuppressWarnings("unused")
	public String getError() {
		return message;
	}

}