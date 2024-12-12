package fr.matthieu.chatop.model;


import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "rentals")
public class Rental {


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

	@Column(name = "description", nullable = false)
	private String description;

	@ManyToOne
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;


	/**
	 * Default constructor required by JPA.
	 * This constructor should not be used in application code.
	 */
	@Deprecated
	protected Rental() {}

	public Rental(String name, Double surface, BigDecimal price, String picture, String description, User owner) {
		this.name = name;
		this.surface = surface;
		this.price = price;
		this.picture = picture;
		this.description = description;
		this.owner = owner;
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
