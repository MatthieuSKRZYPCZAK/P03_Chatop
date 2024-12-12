package fr.matthieu.chatop.common;

/**
 * A utility class that defines constant messages used in API responses.
 * This class centralizes common response messages.
 */
public class ResponseMessages {

	private ResponseMessages() {}

	public static final String REGISTER_FAILED = "register failed";

	public static final String EMAIL_ALREADY_IN_USE = "email address already in use";

	public static final String INVALID_CREDENTIALS = "invalid credentials";

	public static final String UNAUTHORIZED_ACCESS = "unauthorized access";

	public static final String USER_NOT_FOUND = "user not found";

	public static final String RENTAL_NOT_FOUND = "Rental with ID %d not found";

	public static final String RENTAL_CREATED = "Rental created !";

	public static final String RENTAL_UPDATED = "Rental updated !";

	public static final String SERVICE_UNAVAILABLE = "service unavailable";

	public static final String JWT_EXPIRED = "JWT token has expired";
}
