package fr.matthieu.chatop.service;

import fr.matthieu.chatop.common.ResponseMessages;
import fr.matthieu.chatop.dto.CreateRentalDTO;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.exception.RentalNotFoundException;
import fr.matthieu.chatop.mapper.RentalMapper;
import fr.matthieu.chatop.model.Rental;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.repository.RentalRepository;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_NOT_FOUND;

@Slf4j
@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final RentalMapper rentalMapper;
	private final UserService userService;

	@Autowired
	public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper, UserService userService) {
		this.rentalRepository = rentalRepository;
		this.rentalMapper = rentalMapper;
		this.userService = userService;
	}

	public List<RentalDTO> getAllRentalsDTO() {
		List<Rental> rentals = rentalRepository.findAll();
		return rentals.stream()
				.map(rentalMapper::toDTO)
				.toList();
	}

	public RentalDTO getRentalDTOById(Long id) {
		Rental rental = rentalRepository.findById(id)
				.orElseThrow(() -> new RentalNotFoundException(
						String.format(RENTAL_NOT_FOUND, id)
				));
		return rentalMapper.toDTO(rental);
	}

	public boolean createRental(@Valid CreateRentalDTO createRentalDTO) {
		String picture = "url";
		User user = userService.getAuthenticateUser();
		Rental rental = new Rental(createRentalDTO.name(), createRentalDTO.surface(), createRentalDTO.price(), picture, createRentalDTO.description(), user);
		return true;
	}
}
