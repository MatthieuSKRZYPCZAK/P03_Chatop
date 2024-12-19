package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.dto.UserDTO;
import fr.matthieu.chatop.exception.UnauthorizedException;
import fr.matthieu.chatop.exception.UserAlreadyExistsException;
import fr.matthieu.chatop.exception.UserNotFoundException;
import fr.matthieu.chatop.model.UserEntity;
import fr.matthieu.chatop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static fr.matthieu.chatop.common.ResponseMessages.*;

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
		Optional<UserEntity> userExist = userRepository.findByEmail(registerDTO.email());
		if (userExist.isPresent()) {
			throw new UserAlreadyExistsException(EMAIL_ALREADY_IN_USE);
		}
		UserEntity userEntity = convertToUser(registerDTO);
		UserEntity savedUserEntity = userRepository.save(userEntity);
		return jwtService.generate(savedUserEntity);
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
	 * @return The {@link UserEntity} object.
	 * @throws UsernameNotFoundException If the user does not exist.
	 */
	public UserEntity getUserById(Long id) {
		return this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND) );
	}

	/**
	 * Converts a {@link RegisterDTO} into a {@link UserEntity} object.
	 *
	 * @param registerDTO The registration details provided by the client.
	 * @return A {@link UserEntity} object.
	 */
	private UserEntity convertToUser(RegisterDTO registerDTO) {
		return new UserEntity(
				registerDTO.email().toLowerCase(),
				passwordEncoder.encode(registerDTO.password()),
				registerDTO.name()
		);
	}

	/**
	 * Retrieves the currently authenticated user from the security context.
	 *
	 * @return The {@link UserEntity} object of the authenticated user.
	 */
	public UserEntity getAuthenticateUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication.getPrincipal() == null || !(authentication.getPrincipal() instanceof UserEntity)) {
			throw new UnauthorizedException(UNAUTHORIZED_ACCESS);
		}
		return (UserEntity) authentication.getPrincipal();
	}

	/**
	 * Constructs a {@link UserDTO} for the currently authenticated user.
	 *
	 * @return A {@link UserDTO} containing user details.
	 */
	public UserDTO getUserResponseDTO() {
		UserEntity userEntity = getAuthenticateUser();
		return convertToResponseDTO(userEntity);
	}

	/**
	 * Increments the token version for a user and saves the changes to the database.
	 *
	 * @param userEntity The {@link UserEntity} whose token version is to be incremented.
	 */
	public void incrementTokenVersion(UserEntity userEntity) {
		userEntity.incrementTokenVersion();
		userRepository.save(userEntity);
	}

	/**
	 * Retrieves a {@link UserDTO} for a user by their ID.
	 *
	 * @param id The ID of the user.
	 * @return A {@link UserDTO} containing user details.
	 */
	public UserDTO getUserDTOById(Long id) {
		return convertToResponseDTO(getUserById(id));
	}

	/**
	 * Converts a {@link UserEntity} object into a {@link UserDTO}.
	 *
	 * @param userEntity The {@link UserEntity} object to be converted.
	 * @return A {@link UserDTO} containing user details.
	 */
	private UserDTO convertToResponseDTO(UserEntity userEntity) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		String createdAtFormatted = userEntity.getCreatedAt().format(formatter);
		LocalDateTime updatedAt = userEntity.getUpdatedAt();
		String updatedAtFormatted = updatedAt != null ? updatedAt.format(formatter) : null;

		return new UserDTO(
				userEntity.getId(),
				userEntity.getName(),
				userEntity.getEmail(),
				createdAtFormatted,
				updatedAtFormatted
		);
	}
}