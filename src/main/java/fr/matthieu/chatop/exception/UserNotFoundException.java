package fr.matthieu.chatop.exception;

/**
 * Exception thrown when a rental with a specific ID is not found.
 */
public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String message) {
		super(message);
	}
}
