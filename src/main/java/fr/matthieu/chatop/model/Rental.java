package fr.matthieu.chatop.model;


import jakarta.persistence.*;
import lombok.Data;

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
	private Integer surface;

	@Column(name = "price", nullable = false)
	private Integer price;

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





}
