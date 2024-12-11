package fr.matthieu.chatop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.matthieu.chatop.common.ErrorResponse;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.service.JWTService;
import fr.matthieu.chatop.service.UserService;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static fr.matthieu.chatop.common.ResponseMessages.UNAUTHORIZED_ACCESS;
import static fr.matthieu.chatop.common.ApiRoutes.*;


/**
 * JWT Authentication Filter that is executed for every incoming HTTP request.
 * This filter validates the presence and authenticity of a JWT token and updates
 * the security context for authenticated users.
 */
@Slf4j
@Service
public class JwtAuthFilter extends OncePerRequestFilter {

	private final UserService userService;
	private final JWTService jwtService;
	private final ObjectMapper objectMapper;

	@Autowired
	public JwtAuthFilter(UserService userService, JWTService jwtService, ObjectMapper objectMapper) {
		this.userService = userService;
		this.jwtService = jwtService;
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
		log.info("JWT Auth Filter");
		String token;
		String username;
		boolean isTokenExpired;

		final String authorization = request.getHeader("Authorization");

		if(authorization != null && authorization.startsWith("Bearer ")) {
			token = authorization.substring(7);
			try {
				isTokenExpired = jwtService.isTokenExpired(token);
				username = jwtService.extractUsername(token);
			} catch (MalformedJwtException e) {
				// If the token is malformed, send an error response and stop processing
				sendErrorResponse(response);
				return;
			}
		} else {
			sendErrorResponse(response);
			return;
		}

		// Validate the token and authenticate the user
		if(!isTokenExpired && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails =  userService.loadUserByUsername(username);
			Integer tokenVersion = jwtService.getClaim(token, claims -> claims.get("tokenVersion", Integer.class));

			// Check if the token version matches the user's current token version
			if (tokenVersion != null && tokenVersion.equals(((User) userDetails).getTokenVersion())) {
				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			} else {
				sendErrorResponse(response);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}

	/**
	 * Determines if the filter should not be applied for specific routes.
	 *
	 * @param request Incoming HTTP request.
	 * @return {@code true} if the filter should be skipped for this request, otherwise {@code false}.
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {

		String path = request.getRequestURI();
		boolean shouldNotFilter = path.startsWith(REGISTER_URL) ||
				path.startsWith(LOGIN_URL) ||
				path.startsWith(SWAGGER_UI_URL) ||
				path.startsWith(API_DOCS_URL);
		log.info("Should not filter {}: {}", path, shouldNotFilter);
		return shouldNotFilter;
	}

	/**
	 * Sends an error response when authentication fails.
	 *
	 * @param response Outgoing HTTP response.
	 * @throws IOException If an error occurs while writing the JSON response.
	 */
	private void sendErrorResponse(HttpServletResponse response) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(UNAUTHORIZED_ACCESS);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String json = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(json);
	}
}