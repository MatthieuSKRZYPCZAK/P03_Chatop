package fr.matthieu.chatop.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "messages")
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String message;

	@Column(name="created_at")
	private LocalDateTime createdAt;

	@Column(name="updated_at")
	private LocalDateTime updatedAt;
}
