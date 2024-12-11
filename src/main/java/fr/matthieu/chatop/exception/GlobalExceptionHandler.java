package fr.matthieu.chatop.exception;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.common.ResponseMessages;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for the application.
 * This class handles specific exceptions and returns appropriate HTTP responses with error details.
 * It uses Spring's {@code @ControllerAdvice} to intercept exceptions across the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * Handles the {@link UserAlreadyExistsException}.
	 * This exception is thrown when attempting to register a user with an email
	 * that already exists in the system.
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 400 (Bad Request).
	 */
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link MethodArgumentNotValidException}.
	 * This exception is thrown when validation on a method argument fails (invalid DTO fields).
	 *
	 * @param ex The exception instance containing validation errors.
	 * @return A {@link ResponseEntity} with a map of field errors and HTTP status 400 (Bad Request).
	 *         The response includes field names as keys and validation error messages as values.
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(
				error -> errors.put(error.getField(), error.getDefaultMessage())
		);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
	}

	/**
	 * Handles the {@link BadCredentialsException}.
	 * This exception is thrown when authentication fails due to invalid credentials.
	 *
	 * @param e The exception instance.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 401 (Unauthorized).
	 */
	@ExceptionHandler(BadCredentialsException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> handleBadCredentials(BadCredentialsException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(ResponseMessages.INVALID_CREDENTIALS));
	}

}
