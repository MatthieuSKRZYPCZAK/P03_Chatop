package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService implements UserDetailsService {

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	public void register(RegisterDTO registerDTO) {
		Optional<User> userExist = userRepository.findByEmail(registerDTO.email());
		if (userExist.isPresent()) {
			throw new IllegalArgumentException("Email address already in use");
		}
		User user = convertToUser(registerDTO);
		userRepository.save(user);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return this.userRepository
				.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found") );
	}

	public User getUserById(Long id) {
		return this.userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found") );
	}

	private User convertToUser(RegisterDTO registerDTO) {
		User user = new User();
		user.setEmail(registerDTO.email().toLowerCase());
		user.setPassword(passwordEncoder.encode(registerDTO.password()));
		user.setName(registerDTO.name());
		return user;
	}

	public User getAuthenticateUser() {
		return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}
}
