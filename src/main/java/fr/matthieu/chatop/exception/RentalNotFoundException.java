package fr.matthieu.chatop.exception;

/**
 * Exception thrown when a rental with a specific ID is not found.
 */
public class RentalNotFoundException extends RuntimeException {
	public RentalNotFoundException(String message) {
		super(message);
	}
}
