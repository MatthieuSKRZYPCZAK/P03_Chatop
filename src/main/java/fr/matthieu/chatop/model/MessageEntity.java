package fr.matthieu.chatop.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class MessageEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Message content is required")
	@Column(name="message", nullable=false, length = 1000)
	private String message;

	@NotNull(message = "Sender is required.")
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity sender;

	@NotNull(message = "Rental is required.")
	@ManyToOne
	@JoinColumn(name = "rental_id", nullable=false)
	private RentalEntity rentalEntity;


	@Column(name="created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name="updated_at")
	private LocalDateTime updatedAt;

	/**
	 * Default constructor required by JPA.
	 * This constructor should not be used in application code.
	 */
	@Deprecated
	protected MessageEntity() {}

	/**
	 * Constructs a new {@code Message} with the specified content, sender, and rental.
	 *
	 * @param message The content of the message.
	 * @param sender  The user sending the message.
	 * @param rentalEntity  The rental related to the message.
	 */
	public MessageEntity(String message, UserEntity sender, RentalEntity rentalEntity) {
		this.message = message;
		this.sender = sender;
		this.rentalEntity = rentalEntity;
	}


	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = null;
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
