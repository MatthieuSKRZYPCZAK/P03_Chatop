package fr.matthieu.chatop.exception;

/**
 * Exception thrown when an error occurs during file storage operations.
 */
public class FileStorageException extends RuntimeException {
	public FileStorageException(String message) {
		super(message);
	}
}
