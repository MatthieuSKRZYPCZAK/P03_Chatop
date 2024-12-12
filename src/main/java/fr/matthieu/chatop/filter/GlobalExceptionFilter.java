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
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static fr.matthieu.chatop.common.ResponseMessages.JWT_EXPIRED;
import static fr.matthieu.chatop.common.ResponseMessages.SERVICE_UNAVAILABLE;


@Slf4j
@Service
public class GlobalExceptionFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Autowired
	public GlobalExceptionFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (DataAccessResourceFailureException e) {
			log.warn("DataAccessResourceFailure Exception : " + e.getMessage());
			sendErrorResponse(response,HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE);
		} catch (ExpiredJwtException e) {
			log.warn("ExpiredJwtException : " + e.getMessage());
			sendErrorResponse(response,HttpStatus.UNAUTHORIZED, JWT_EXPIRED);
		}
	}

	private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
		ErrorResponse errorResponse = new ErrorResponse(message);
		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String json = objectMapper.writeValueAsString(errorResponse);
		response.getWriter().write(json);
	}
}
