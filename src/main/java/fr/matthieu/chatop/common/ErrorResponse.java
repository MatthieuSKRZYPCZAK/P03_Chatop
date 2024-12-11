package fr.matthieu.chatop.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A class representing the structure of an error response sent by the API.
 */
@Schema(name = "Error Response", description = "Represents an error response with a descriptive message")
public class ErrorResponse {
	private String message;

	public ErrorResponse(String message) {
		this.message = message;
	}

	public String getError() {
		return message;
	}

	public ErrorResponse setError(String message) {
		this.message = message;
		return this;
	}
}