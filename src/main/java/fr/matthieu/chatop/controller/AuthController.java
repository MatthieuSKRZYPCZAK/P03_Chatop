package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.dto.AuthenticationDTO;
import fr.matthieu.chatop.dto.RegisterDTO;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.service.JWTService;
import fr.matthieu.chatop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static fr.matthieu.chatop.controller.ApiRoutes.*;

@Slf4j
@RestController
public class AuthController {

	public JWTService jwtService;
    private final UserService userService;
	private final AuthenticationManager authenticationManager;

	@Autowired
	public AuthController(UserService userService, AuthenticationManager authenticationManager, JWTService jwtService) {
		this.jwtService = jwtService;
		this.userService = userService;
		this.authenticationManager = authenticationManager;
	}

	@PostMapping(LOGIN_URL)
	public ResponseEntity<Object> login(@RequestBody AuthenticationDTO authenticationDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
		);
		log.info("result {}", authentication.isAuthenticated());
		if (authentication.isAuthenticated()) {
			return ResponseEntity.ok().body(this.jwtService.generate(authenticationDTO.username()));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}
	}

	@PostMapping(REGISTER_URL)
	public ResponseEntity<Object> register(@Valid @RequestBody RegisterDTO registerDTO) {
		log.info("Registering new user");
		userService.register(registerDTO);
		return ResponseEntity.ok().body("User registered successfully");
	}

	@GetMapping(ME_URL)
	public ResponseEntity<Object> getUser() {
		System.out.println("ddans me");
		User user = userService.getAuthenticateUser();
		try {
			return ResponseEntity.ok().body(user);
		} catch(Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error " + e.getMessage());
		}

	}

}
