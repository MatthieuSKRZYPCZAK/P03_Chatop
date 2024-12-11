package fr.matthieu.chatop.mapper;

import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.model.Rental;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
public class RentalMapper {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

	public RentalDTO toDTO(Rental rental) {
		return new RentalDTO(
				rental.getId(),
				rental.getName(),
				rental.getSurface(),
				rental.getPrice(),
				rental.getPicture(),
				rental.getDescription(),
				rental.getOwner().getId(),
				rental.getCreatedAt().format(FORMATTER),
				rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(FORMATTER) : null
		);
	}
}
