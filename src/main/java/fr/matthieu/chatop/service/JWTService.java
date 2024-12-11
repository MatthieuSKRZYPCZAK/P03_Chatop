package fr.matthieu.chatop.service;


import fr.matthieu.chatop.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Service for handling JWT (JSON Web Token) operations.
 * This includes token generation, validation, and claims extraction.
 */
@Service
public class JWTService {

	@Value("${encryption.key}")
	private String encryptionKey;


	/**
	 * Generates a JWT token for the given user.
	 *
	 * @param user The user for whom the token is being generated.
	 * @return A JWT token as a string.
	 */
	public String generate(User user) {
		Map<String, String> tokenMap = this.generateJwt(user);
		return tokenMap.get("bearer");
	}

	/**
	 * Extracts the username (subject) from the provided token.
	 *
	 * @param token The JWT token.
	 * @return The username (email) extracted from the token.
	 */
	public String extractUsername(String token) {
		return this.getClaim(token, Claims::getSubject);
	}

	/**
	 * Checks whether the provided token has expired.
	 *
	 * @param token The JWT token.
	 * @return {@code true} if the token has expired, otherwise {@code false}.
	 */
	public boolean isTokenExpired(String token) {
		Date expirationDate = this.getClaim(token,Claims::getExpiration);
		return expirationDate.before(new Date());
	}

	/**
	 * Extracts a specific claim from the provided token.
	 *
	 * @param token    The JWT token.
	 * @param function A function defining how to extract the desired claim.
	 * @param <T>      The type of the claim to extract.
	 * @return The extracted claim.
	 */
	public <T> T getClaim(String token, Function<Claims, T> function){
		Claims claims = getAllClaims(token);
		return function.apply(claims);
	}

	/**
	 * Retrieves all claims from the provided token.
	 *
	 * @param token The JWT token.
	 * @return All claims extracted from the token.
	 */
	private Claims getAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(this.getKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	/**
	 * Generates a JWT token containing claims for the given user.
	 *
	 * @param user The user for whom the token is being generated.
	 * @return A map containing the generated token with the key "bearer".
	 */
	private Map<String, String> generateJwt(User user) {

		final long currentTime = System.currentTimeMillis();
		final long expirationTime = currentTime + 30 * 60 * 1000; // 30mn

		final Map<String, Object> claims = Map.of(
				"name", user.getName(),
				Claims.EXPIRATION, new Date(expirationTime),
				Claims.SUBJECT, user.getEmail(),
				"tokenVersion", user.getTokenVersion()
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

	/**
	 * Retrieves the signing key used for generating and validating tokens.
	 * The key is derived from the Base64-decoded encryption key.
	 *
	 * @return A {@link Key} object for signing and validating tokens.
	 */
	private Key getKey() {
		final byte[] decoder = Decoders.BASE64.decode(encryptionKey);
		return Keys.hmacShaKeyFor(decoder);
	}
}
