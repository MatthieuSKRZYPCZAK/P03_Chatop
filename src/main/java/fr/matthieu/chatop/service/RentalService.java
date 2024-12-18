package fr.matthieu.chatop.service;

import fr.matthieu.chatop.dto.CreateRentalDTO;
import fr.matthieu.chatop.dto.RentalDTO;
import fr.matthieu.chatop.exception.RentalNotFoundException;
import fr.matthieu.chatop.exception.UnauthorizedException;
import fr.matthieu.chatop.model.Rental;
import fr.matthieu.chatop.model.User;
import fr.matthieu.chatop.repository.RentalRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static fr.matthieu.chatop.common.ResponseMessages.RENTAL_NOT_FOUND;
import static fr.matthieu.chatop.common.ResponseMessages.UNAUTHORIZED_ACCESS;

/**
 * Service class for managing rental-related operations.
 */
@Slf4j
@Service
public class RentalService {

	private final RentalRepository rentalRepository;
	private final UserService userService;
	private final UploadService uploadService;

	@Autowired
	public RentalService(RentalRepository rentalRepository, UserService userService, UploadService uploadService) {
		this.rentalRepository = rentalRepository;
		this.userService = userService;
		this.uploadService = uploadService;
	}

	/**
	 * Retrieves all rentals and converts them into DTOs.
	 *
	 * @return A list of {@link RentalDTO} objects representing all rentals.
	 */
	public List<RentalDTO> getAllRentalsDTO() {
		List<Rental> rentals = rentalRepository.findAll();
		return rentals.stream()
				.map(this::convertToDTO)
				.toList();
	}

	/**
	 * Retrieves a rental by its ID and converts it into a DTO.
	 *
	 * @param id The ID of the rental to retrieve.
	 * @return A {@link RentalDTO} representing the rental.
	 */
	public RentalDTO getRentalDTOById(Long id) {
		Rental rental = getRentalById(id);
		return convertToDTO(rental);
	}

	/**
	 * Creates a new rental and saves it to the database.
	 * <p>
	 * The rental is associated with the currently authenticated user. The picture
	 * is uploaded and its URL is saved with the rental.
	 * </p>
	 * @param createRentalDTO The details of the rental to create.
	 */
	@Transactional
	public void createRental(CreateRentalDTO createRentalDTO) {
		User user = userService.getAuthenticateUser();
		String picture = uploadService.storePicture(createRentalDTO.picture(), user.getName());
		Rental rental = new Rental(createRentalDTO.name(), createRentalDTO.surface(), createRentalDTO.price(), picture, createRentalDTO.description(), user);
		rentalRepository.save(rental);
	}

	/**
	 * Retrieves a rental by its ID.
	 *
	 * @param id The ID of the rental to retrieve.
	 * @return The {@link Rental} object
	 */
	protected Rental getRentalById(Long id) {
		return rentalRepository.findById(id)
				.orElseThrow(() -> new RentalNotFoundException(
						String.format(RENTAL_NOT_FOUND, id)
				));
	}

	/**
	 * Verifies that the currently authenticated user is the owner of the specified rental.
	 *
	 * @param id The ID of the rental to check.
	 * @return The {@link Rental} object if the user is the owner.
	 */
	public Rental checkOwner(Long id) {
		User user = userService.getAuthenticateUser();
		Rental rental = getRentalById(id);
		if(rental.getOwner().equals(user)) {
			return rental;
		}
		throw new UnauthorizedException(UNAUTHORIZED_ACCESS);
	}

	/**
	 * Updates the details of an existing rental.
	 *
	 * @param rental           The {@link Rental} object to update.
	 * @param createRentalDTO  The new details for the rental.
	 */
	@Transactional
	public void UpdateRental(Rental rental, CreateRentalDTO createRentalDTO) {

		rental.setName(createRentalDTO.name());
		rental.setSurface(createRentalDTO.surface());
		rental.setPrice(createRentalDTO.price());
		rental.setDescription(createRentalDTO.description());

		rentalRepository.save(rental);
	}

	/**
	 * Converts a {@link Rental} object into a {@link RentalDTO}.
	 *
	 * @param rental The {@link Rental} object to convert.
	 * @return A {@link RentalDTO} containing the rental details.
	 */
	private RentalDTO convertToDTO(Rental rental) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return new RentalDTO(
				rental.getId(),
				rental.getName(),
				rental.getSurface(),
				rental.getPrice(),
				rental.getPicture(),
				rental.getDescription(),
				rental.getOwner().getId(),
				rental.getCreatedAt().format(formatter),
				rental.getUpdatedAt() != null ? rental.getUpdatedAt().format(formatter) : null
		);
	}
}
