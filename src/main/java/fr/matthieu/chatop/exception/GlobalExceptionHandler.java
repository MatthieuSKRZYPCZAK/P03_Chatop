package fr.matthieu.chatop.exception;

import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.common.ResponseMessages;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized exception handler for the application.
 * This class handles specific exceptions and returns appropriate HTTP responses with error details.
 * It uses Spring's {@code @ControllerAdvice} to intercept exceptions across the application.
 */
@Slf4j
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
		log.info("Handling UserAlreadyExistsException - User with the given email already exists.");
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
		log.info("Handling MethodArgumentNotValidException - Validation failed for method arguments.");
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
		log.info("Handling BadCredentialsException - Invalid credentials provided for authentication. - {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(new ErrorResponse(ResponseMessages.INVALID_CREDENTIALS));
	}

	/**
	 * Handles the {@link RentalNotFoundException}.
	 * This exception is thrown when a rental is not found in the system.
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 404 (Not Found).
	 */
	@ExceptionHandler(RentalNotFoundException.class)
	public ResponseEntity<Object> handleRentalNotFoundException(RentalNotFoundException e) {
		log.info("Handling RentalNotFoundException - Rental not found.");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link JwtException}.
	 * This exception is thrown when there is an issue with JWT processing.
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 401 (Unauthorized).
	 */
	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ErrorResponse> handleJwtException(JwtException e) {
		log.info("Handling JwtException - Issue with JWT processing.");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link UnauthorizedException}.
	 * <p>
	 * Thrown when a user attempts to access a resource they are not authorized to access.
	 * </p>
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 401 (Unauthorized).
	 */
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException e) {
		log.info("Handling UnauthorizedException - Unauthorized.");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link UserNotFoundException}.
	 * <p>
	 * Thrown when a user cannot be found by the given identifier.
	 * </p>
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 404 (Not Found).
	 */
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException e) {
		log.info("Handling UserNotFoundException - User not found.");
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link FileStorageException}.
	 * <p>
	 * Thrown when there is an error during file storage operations, such as saving or retrieving files.
	 * </p>
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 500 (Internal Server Error).
	 */
	@ExceptionHandler(FileStorageException.class)
	public ResponseEntity<ErrorResponse> handleFileStorageException(FileStorageException e) {
		log.info("Handling FileStorageException - File storage error.");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(e.getMessage()));
	}

	/**
	 * Handles the {@link MaxUploadSizeExceededException}.
	 * <p>
	 * Thrown when a file upload exceeds the maximum allowed size.
	 * </p>
	 *
	 * @param e The exception instance containing the error message.
	 * @return A {@link ResponseEntity} with an error message and HTTP status 400 (Bad Request).
	 */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		log.info("MaxUploadSizeExceededException : {}", e.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
	}

}