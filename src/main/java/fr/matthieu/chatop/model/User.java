package fr.matthieu.chatop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	@Email(message = "Email should be valid")
	@NotBlank(message = "Email cannot be blank")
	private String email;

	@Column(nullable = false)
	@NotBlank(message = "Password cannot be blank")
	@Size(min = 8, max = 128, message = "Password must be between {min} and {max} characters")
	private String password;

	@Column(nullable = false)
	@NotBlank(message = "Name cannot be blank")
	@Size(min = 3, max = 20, message = "Name must be between {min} and {max} characters")
	private String name;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "token_version")
	private Integer tokenVersion;


	/**
	 * Default constructor required by JPA.
	 * This constructor should not be used in application code.
	 */
	public User() {}

	public User(String email, String password, String name) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.tokenVersion = 1;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// No granted authorities are currently associated with this user.
		return List.of();
	}

	@Override
	public String getUsername() {
		return email;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = null;
		normalizeEmail();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
		normalizeEmail();
	}

	/**
	 * Normalize the email address to a lowercase form
	 * to ensure consistent lookups and comparisons.
	 */
	private void normalizeEmail() {
		if(this.email != null) {
			this.email = this.email.toLowerCase();
		}
	}

	public void incrementTokenVersion() {
		this.tokenVersion++;
	}
}
