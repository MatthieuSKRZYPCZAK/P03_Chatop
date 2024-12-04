package fr.matthieu.chatop.filter;

import fr.matthieu.chatop.controller.ApiRoutes;
import fr.matthieu.chatop.service.JWTService;
import fr.matthieu.chatop.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Service
public class JwtAuthFilter extends OncePerRequestFilter {

//	private static final RequestMatcher POST_REGISTER_URL_MATCHER = new AntPathRequestMatcher(ApiRoutes.REGISTER_URL, "POST");
//	private static final RequestMatcher POST_LOGIN_URL_MATCHER = new AntPathRequestMatcher(ApiRoutes.LOGIN_URL, "POST");

	private final UserService userService;
	private final JWTService jwtService;

	@Autowired
	public JwtAuthFilter(UserService userService, JWTService jwtService) {
		this.userService = userService;
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		log.info("JWT Auth Filter");
		String token = null;
		String username = null;
		boolean isTokenExpired = true;

		final String authorization = request.getHeader("Authorization");
		if(authorization != null && authorization.startsWith("Bearer ")) {
			token = authorization.substring(7);
			isTokenExpired = jwtService.isTokenExpired(token);
			username = jwtService.extractUsername(token);
		}

		if(!isTokenExpired && username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails =  userService.loadUserByUsername(username);
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
		}

		filterChain.doFilter(request, response);
	}
}
