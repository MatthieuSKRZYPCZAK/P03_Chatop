package fr.matthieu.chatop.common;

/**
 * A class representing the structure of an error response sent by the API.
 */
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