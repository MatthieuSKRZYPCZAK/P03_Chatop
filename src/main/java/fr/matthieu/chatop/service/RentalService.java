package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.CreateRentalDTO;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.exception.RentalNotFoundException;
import fr.matthieu.chatop.exception.UnauthorizedException;
import fr.matthieu.chatop.mapper.RentalMapper;
import fr.matthieu.chatop.model.Rental;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_NOT_FOUND;
import static fr.matthieu.chatop.common.ResponseMessages.UNAUTHORIZED_ACCESS;

@Slf4j
@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final RentalMapper rentalMapper;
	private final UserService userService;
	private final UploadService uploadService;


	@Autowired
	public RentalService(RentalRepository rentalRepository, RentalMapper rentalMapper, UserService userService, UploadService uploadService) {
		this.rentalRepository = rentalRepository;
		this.rentalMapper = rentalMapper;
		this.userService = userService;
		this.uploadService = uploadService;
	}

	public List<RentalDTO> getAllRentalsDTO() {
		List<Rental> rentals = rentalRepository.findAll();
		return rentals.stream()
				.map(rentalMapper::toDTO)
				.toList();
	}

	public RentalDTO getRentalDTOById(Long id) {
		Rental rental = getRentalById(id);
		return rentalMapper.toDTO(rental);
	}

	@Transactional
	public void createRental(CreateRentalDTO createRentalDTO) {
		User user = userService.getAuthenticateUser();
		String picture = uploadService.storePicture(createRentalDTO.picture(), user.getName());
		Rental rental = new Rental(createRentalDTO.name(), createRentalDTO.surface(), createRentalDTO.price(), picture, createRentalDTO.description(), user);
		rentalRepository.save(rental);
	}

	private Rental getRentalById(Long id) {
		return rentalRepository.findById(id)
				.orElseThrow(() -> new RentalNotFoundException(
						String.format(RENTAL_NOT_FOUND, id)
				));
	}

	public Rental checkOwner(Long id) {
		User user = userService.getAuthenticateUser();
		Rental rental = getRentalById(id);
		if(rental.getOwner().equals(user)) {
			return rental;
		}
		throw new UnauthorizedException(UNAUTHORIZED_ACCESS);
	}

	@Transactional
	public void UpdateRental(Rental rental, CreateRentalDTO createRentalDTO) {

		rental.setName(createRentalDTO.name());
		rental.setSurface(createRentalDTO.surface());
		rental.setPrice(createRentalDTO.price());
		rental.setDescription(createRentalDTO.description());

		rentalRepository.save(rental);
	}
}
