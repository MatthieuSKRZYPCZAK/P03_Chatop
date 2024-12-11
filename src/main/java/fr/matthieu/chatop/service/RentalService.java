package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.mapper.RentalMapper;
import fr.matthieu.chatop.model.Rental;
import fr.matthieu.chatop.repository.RentalRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final RentalMapper rentalMapper;

	@Autowired
	public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper) {
		this.rentalRepository = rentalRepository;
		this.rentalMapper = rentalMapper;
	}

	public List<RentalDTO> getAllRentalsDTO() {
		List<Rental> rentals = rentalRepository.findAll();
		return rentals.stream()
				.map(rentalMapper::toDTO)
				.toList();
	}

}
