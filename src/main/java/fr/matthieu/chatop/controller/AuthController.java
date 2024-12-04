package fr.matthieu.chatop.controller;

import fr.matthieu.chatop.dto.AuthenticationDTO;
import fr.matthieu.chatop.service.JWTService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static fr.matthieu.chatop.controller.ApiRoutes.*;

@Slf4j
@RestController
public class AuthController {

	public JWTService jwtService;

	private AuthenticationManager authenticationManager;

	public AuthController(JWTService jwtService) {
		this.jwtService = jwtService;
	}

//	@PostMapping(LOGIN_URL)
//	public String getToken(Authentication authentication) {
//		return jwtService.generateToken(authentication);
//	}

	@PostMapping(LOGIN_URL)
	public Map<String, String> login(@RequestBody AuthenticationDTO authenticationDTO) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationDTO.username(), authenticationDTO.password())
		);
		log.info("result {}", authentication.isAuthenticated());
		return null;
	}
}
