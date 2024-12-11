package fr.matthieu.chatop.dto;

/**
 * Data Transfer Object (DTO) representing the authentication credentials.
 * This class is used to encapsulate the username and password provided by the client during login.
 *
 * @param username The username or email address of the user attempting to log in.
 * @param password The password of the user attempting to log in.
 */
public record AuthenticationDTO(String username, String password) {
}
