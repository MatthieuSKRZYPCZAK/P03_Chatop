package fr.matthieu.chatop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rentals")
public class RentalEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "surface", nullable = false)
	private Double surface;

	@Column(name = "price", nullable = false)
	private BigDecimal price;

	@Column(name = "picture", nullable = false)
	private String picture;

	@Column(name = "description", nullable = false, length = 1000)
	private String description;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private UserEntity owner;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;


	/**
	 * Default constructor required by JPA.
	 * This constructor should not be used in application code.
	 */
	@Deprecated
	protected RentalEntity() {}

	public RentalEntity(String name, Double surface, BigDecimal price, String picture, String description, UserEntity owner) {
		this.name = name;
		this.surface = surface;
		this.price = price;
		this.picture = picture;
		this.description = description;
		this.owner = owner;
	}

	/**
	 * Sets timestamps before the entity is persisted.
	 */
	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = null;
	}

	/**
	 * Updates the timestamp before the entity is updated.
	 */
	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

}