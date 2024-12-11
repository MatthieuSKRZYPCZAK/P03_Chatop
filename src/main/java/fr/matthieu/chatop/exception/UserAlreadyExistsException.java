package fr.matthieu.chatop.exception;

/**
 * Custom exception thrown when attempting to register a user with an email
 * that already exists in the system.
 */
public class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
