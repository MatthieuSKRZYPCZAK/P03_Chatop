package fr.matthieu.chatop.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.matthieu.chatop.common.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static fr.matthieu.chatop.common.ResponseMessages.*;

/**
 * Filter class for handling global exceptions during request processing.
 * <p>
 * This filter intercepts requests and responses, allowing centralized handling of specific exceptions
 * that may occur during request processing, such as database connectivity issues or expired JWT tokens.
 * </p>
 *
 * <p>
 * The filter ensures that consistent error responses are returned to the client in JSON format,
 * along with the appropriate HTTP status codes.
 * </p>
 */
@Slf4j
@Service
public class GlobalExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	/**
	 * Constructs a {@code GlobalExceptionFilter} with the required dependencies.
	 *
	 * @param objectMapper The {@link ObjectMapper} used for serializing error responses to JSON.
	 */
	@Autowired
	public GlobalExceptionFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (DataAccessResourceFailureException e) {
			log.warn("DataAccessResourceFailure Exception : {}", e.getMessage());
			sendErrorResponse(response, HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE);
		} catch (ExpiredJwtException e) {
			log.warn("ExpiredJwtException : {}", e.getMessage());
			sendErrorResponse(response,HttpStatus.UNAUTHORIZED, JWT_EXPIRED);
		}
	}

	/**
	 * Sends a standardized JSON error response to the client.
	 *
	 * @param response The {@link HttpServletResponse} to write the error response to.
	 * @param status   The HTTP status code to set on the response.
	 * @param message  The error message to include in the response body.
	 * @throws IOException If an I/O error occurs during response handling.
	 */
	private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(message);
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String json = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(json);
	}
}
