package fr.matthieu.chatop.service;


import fr.matthieu.chatop.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

	@Value("${encryption.key}")
	private String encryptionKey;

	private UserService userService;

	@Autowired
	public JWTService(UserService userService) {
		this.userService = userService;
	}

	public Map<String, String> generate(String username) {
		User user = (User) this.userService.loadUserByUsername(username);
		return this.generateJwt(user);
	}

	public String extractUsername(String token) {
		return this.getClaim(token, Claims::getSubject);
	}

	public boolean isTokenExpired(String token) {
		Date expirationDate = this.getClaim(token,Claims::getExpiration);
		return expirationDate.before(new Date());
	}

	private <T> T getClaim(String token, Function<Claims, T> function){
		Claims claims = getAllClaims(token);
		return function.apply(claims);
	}

	private Claims getAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(this.getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}


	private Map<String, String> generateJwt(User user) {

		final long currentTime = System.currentTimeMillis();
		final long expirationTime = currentTime + 30 * 60 * 1000; // 30mn

		final Map<String, Object> claims = Map.of(
				"name", user.getName(),
				Claims.EXPIRATION, new Date(expirationTime),
				Claims.SUBJECT, user.getEmail()
		);

		final String bearer = Jwts.builder()
				.setIssuedAt(new Date(currentTime))
				.setExpiration(new Date(expirationTime))
				.setSubject(user.getEmail())
				.setClaims(claims)
				.signWith(getKey(), SignatureAlgorithm.HS256)
				.compact();

		return Map.of("bearer", bearer);
	}

	private Key getKey() {
		final byte[] decoder = Decoders.BASE64.decode(encryptionKey);
		return Keys.hmacShaKeyFor(decoder);
	}

}
