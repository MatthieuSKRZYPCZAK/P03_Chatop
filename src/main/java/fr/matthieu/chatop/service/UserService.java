package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.dto.ResponseUserDTO;
import fr.matthieu.chatop.exception.UserAlreadyExistsException;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static fr.matthieu.chatop.common.ResponseMessages.EMAIL_ALREADY_IN_USE;
import static fr.matthieu.chatop.common.ResponseMessages.USER_NOT_FOUND;

/**
 * Service class for managing user-related operations.
 * This class implements {@link UserDetailsService} to integrate with Spring Security.
 */
@Service
public class UserService implements UserDetailsService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final JWTService jwtService;


	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, JWTService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}


	/**
	 * Registers a new user in the system.
	 *
	 * @param registerDTO The registration details provided by the client.
	 * @return A JWT token for the newly registered user.
	 * @throws UserAlreadyExistsException If the email is already in use.
	 */
	public String register(RegisterDTO registerDTO) {
		Optional<User> userExist = userRepository.findByEmail(registerDTO.email());
		if (userExist.isPresent()) {
			throw new UserAlreadyExistsException(EMAIL_ALREADY_IN_USE);
		}
		User user = convertToUser(registerDTO);
		User savedUser = userRepository.save(user);
		return jwtService.generate(savedUser);
	}

	/**
	 * Loads a user by their username (email).
	 *
	 * @param username The username (email) of the user.
	 * @return The {@link UserDetails} for the user.
	 * @throws UsernameNotFoundException If the user does not exist.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userRepository
				.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND) );
	}

	/**
	 * Retrieves a user by their ID.
	 *
	 * @param id The ID of the user.
	 * @return The {@link User} object.
	 * @throws UsernameNotFoundException If the user does not exist.
	 */
	public User getUserById(Long id) {
		return this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND) );
	}

	/**
	 * Converts a {@link RegisterDTO} into a {@link User} object.
	 *
	 * @param registerDTO The registration details provided by the client.
	 * @return A {@link User} object.
	 */
	private User convertToUser(RegisterDTO registerDTO) {
		return new User(
				registerDTO.email().toLowerCase(),
				passwordEncoder.encode(registerDTO.password()),
				registerDTO.name()
		);
	}

	/**
	 * Retrieves the currently authenticated user from the security context.
	 *
	 * @return The {@link User} object of the authenticated user.
	 */
	public User getAuthenticateUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * Constructs a {@link ResponseUserDTO} for the currently authenticated user.
	 *
	 * @return A {@link ResponseUserDTO} containing user details.
	 */
	public ResponseUserDTO getUserResponseDTO() {
		User user = getAuthenticateUser();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String createdAtFormatted = user.getCreatedAt().format(formatter);
		LocalDateTime updatedAt = user.getUpdatedAt();
		String updatedAtFormatted = updatedAt != null ? updatedAt.format(formatter) : null;

		return new ResponseUserDTO(
				user.getId(),
				user.getName(),
				user.getEmail(),
				createdAtFormatted,
				updatedAtFormatted
		);
	}

	/**
	 * Increments the token version for a user and saves the changes to the database.
	 *
	 * @param user The {@link User} whose token version is to be incremented.
	 */
	public void incrementTokenVersion(User user) {
		user.incrementTokenVersion();
		userRepository.save(user);
	}
}
